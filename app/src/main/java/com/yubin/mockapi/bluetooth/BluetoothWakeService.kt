package com.yubin.mockapi.bluetooth

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yubin.mockapi.R


/**
 * 蓝牙唤醒服务
 * 功能：后台持续监听蓝牙设备连接状态，确保APP能被唤醒
 *
 * @author Claude
 * @date 2025-12-24
 */
class BluetoothWakeService : Service() {

    companion object {
        private const val TAG = "BluetoothWakeService"

        // 启动服务的Action
        const val ACTION_START_SERVICE = "com.yubin.mockapi.bluetooth.START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.yubin.mockapi.bluetooth.STOP_SERVICE"

        // 通知相关
        private const val CHANNEL_ID = "bluetooth_wake_channel"
        private const val NOTIFICATION_ID = 1001
    }

    // 蓝牙适配器
    private var bluetoothAdapter: BluetoothAdapter? = null

    // 蓝牙连接状态广播接收器
    private val connectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: "null"
            Log.d(TAG, "📡 BroadcastReceiver收到广播: action=$action")
            
            if (context == null || intent == null) {
                Log.w(TAG, "⚠️ BroadcastReceiver context或intent为null")
                return
            }

            if (action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                Log.d(TAG, "✅ 收到蓝牙设备连接广播: ACTION_ACL_CONNECTED")
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                
                if (device != null) {
                    val appInForeground = try {
                        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        val runningAppProcesses = activityManager.runningAppProcesses
                        val packageName = context.packageName
                        runningAppProcesses?.any { 
                            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && it.processName == packageName 
                        } ?: false
                    } catch (e: Exception) {
                        false
                    }
                    val appStatus = if (appInForeground) "前台" else "后台"
                    Log.d(TAG, "✅ 检测到蓝牙设备连接: ${device.name} (${device.address})，应用状态: $appStatus")
                    handleDeviceConnected(context, device)
                } else {
                    Log.w(TAG, "⚠️ 蓝牙设备连接广播中未找到设备信息")
                }
            } else {
                Log.d(TAG, "收到其他蓝牙广播: $action")
            }
        }
    }

    // 蓝牙适配器状态变化广播接收器
    private val adapterStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return

            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    Log.d(TAG, "蓝牙已开启，开始监听设备连接")
                    registerDeviceReceiver()
                }
                BluetoothAdapter.STATE_OFF -> {
                    Log.d(TAG, "蓝牙已关闭，停止监听")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🔧 蓝牙唤醒服务已创建")

        // Android 8.0+ 必须调用 startForeground，否则会 ANR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, createNotification())
            Log.d(TAG, "✅ 前台服务已启动 (Android ${Build.VERSION.SDK_INT})")
        }

        // 检测是否是vivo等国产手机
        if (VivoBackgroundHelper.isVivoDevice() || VivoBackgroundHelper.isChineseDevice()) {
            Log.w(TAG, "⚠️ 检测到 ${Build.MANUFACTURER} 手机")
            Log.w(TAG, "⚠️ 注意：在后台时系统可能限制接收蓝牙广播")
            Log.w(TAG, "⚠️ 如需后台唤醒功能，请在设置中开启本应用的后台运行权限")
        }

        // 初始化蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.w(TAG, "❌ 设备不支持蓝牙")
            stopSelf()
            return
        }

        Log.d(TAG, "✅ 蓝牙适配器已初始化")

        // 注册蓝牙适配器状态监听
        registerAdapterStateReceiver()

        // 如果蓝牙已开启，注册设备连接监听
        val isBluetoothEnabled = try {
            bluetoothAdapter?.isEnabled == true
        } catch (e: SecurityException) {
            Log.w(TAG, "⚠️ 检查蓝牙状态需要权限: ${e.message}")
            false
        }
        
        if (isBluetoothEnabled) {
            Log.d(TAG, "✅ 蓝牙已开启，注册设备连接监听")
            registerDeviceReceiver()
        } else {
            Log.d(TAG, "⏳ 蓝牙未开启，等待蓝牙开启后再注册监听")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_START_SERVICE -> {
                    Log.d(TAG, "启动蓝牙唤醒服务")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "停止蓝牙唤醒服务")
                    stopSelf()
                }
                else -> {
                    Log.w(TAG, "未知的action: $action")
                }
            }
        }
        // 返回START_STICKY使服务在被系统杀死后自动重启
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "蓝牙唤醒服务已销毁")

        // Android 8.0+ 需要停止前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        // 注销广播接收器
        try {
            unregisterReceiver(connectionReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "注销设备连接接收器失败: ${e.message}")
        }
        try {
            unregisterReceiver(adapterStateReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "注销适配器状态接收器失败: ${e.message}")
        }
    }

    /**
     * 创建通知渠道（Android 8.0+ 需要）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 普通前台服务通知渠道
            val channel = NotificationChannel(
                CHANNEL_ID,
                "蓝牙唤醒服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "监听蓝牙设备连接，自动唤醒APP"
                setShowBadge(false)
                setSound(null, null)
            }

            // 高优先级全屏通知渠道（用于后台唤醒）
            val wakeChannel = NotificationChannel(
                "${CHANNEL_ID}_wake",
                "蓝牙设备连接提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "蓝牙设备连接时立即提醒"
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(wakeChannel)
        }
    }

    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        val intent = BluetoothWakeActivity.createIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            @Suppress("DEPRECATION")
            android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("蓝牙唤醒服务")
            .setContentText("正在监听蓝牙设备连接...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(false)
            .build()
    }

    /**
     * 更新通知 - 检测到设备连接时调用（使用全屏Intent后台唤醒）
     */
    private fun updateNotification(deviceName: String, deviceAddress: String) {
        val intent = BluetoothWakeActivity.createIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_NAME, deviceName)
            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_ADDRESS, deviceAddress)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        } else {
            @Suppress("DEPRECATION")
            android.app.PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = android.app.PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            pendingIntentFlags
        )

        // 创建全屏Intent（用于后台唤醒）
        val fullScreenIntent = android.app.PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt() + 1,
            intent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(this, "${CHANNEL_ID}_wake")
            .setContentTitle("🔵 蓝牙设备已连接")
            .setContentText("设备: $deviceName - 点击查看详情")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentIntent)
            // 关键：设置全屏Intent，可以在后台直接唤醒页面
            .setFullScreenIntent(fullScreenIntent, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)  // 使用CALL类别，提高优先级
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
        Log.d(TAG, "✅ 全屏通知已发送，设备: $deviceName")
    }

    // 保存最近连接的设备名称
    private var lastConnectedDevice: String? = null

    /**
     * 注册蓝牙适配器状态监听
     */
    private fun registerAdapterStateReceiver() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(adapterStateReceiver, filter)
    }

    /**
     * 注册蓝牙设备连接监听
     */
    private fun registerDeviceReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
            registerReceiver(connectionReceiver, filter)
            Log.d(TAG, "✅ 蓝牙设备连接监听已注册: ACTION_ACL_CONNECTED, ACTION_BOND_STATE_CHANGED")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 注册蓝牙设备连接监听失败: ${e.message}", e)
        }
    }

    /**
     * 检查应用是否在前台
     */
    private fun isAppInForeground(): Boolean {
        return try {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = activityManager.runningAppProcesses ?: return false
            
            val packageName = packageName
            for (processInfo in runningAppProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && processInfo.processName == packageName) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "检查应用前台状态失败: ${e.message}")
            false
        }
    }

    /**
     * 处理蓝牙设备连接
     */
    private fun handleDeviceConnected(context: Context, device: BluetoothDevice) {
        val appInForeground = isAppInForeground()
        val appStatus = if (appInForeground) "前台" else "后台"
        Log.d(TAG, "🔔 处理蓝牙设备连接，应用状态: $appStatus")
        
        // 如果是后台且是vivo等手机，添加警告
        if (!appInForeground && (VivoBackgroundHelper.isVivoDevice() || VivoBackgroundHelper.isChineseDevice())) {
            Log.w(TAG, "⚠️ 当前在后台，vivo等手机可能限制接收广播，但本次已成功接收")
        }
        
        // 检查是否是已配对设备
        val bondState = device.bondState
        if (bondState != BluetoothDevice.BOND_BONDED) {
            Log.d(TAG, "⚠️ 设备未配对，跳过: ${device.name} (bondState=$bondState)")
            return
        }

        // 检查是否重复连接
        if (device.name == lastConnectedDevice) {
            Log.d(TAG, "⚠️ 设备已连接，跳过重复唤醒: ${device.name}")
            return
        }
        lastConnectedDevice = device.name

        Log.d(TAG, "✅ 准备唤醒APP，设备: ${device.name}，当前应用状态: $appStatus")

        // 使用全屏通知唤醒APP（官方推荐方式，可绕过后台启动限制）
        updateNotification(device.name ?: "未知设备", device.address)
        Log.d(TAG, "📢 已发送全屏通知，设备: ${device.name}")
        
        // 如果在前台，可以尝试直接启动（作为备用）
        if (appInForeground) {
            try {
                val wakeIntent = BluetoothWakeActivity.createIntent(context).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(BluetoothWakeActivity.EXTRA_DEVICE_NAME, device.name)
                    putExtra(BluetoothWakeActivity.EXTRA_DEVICE_ADDRESS, device.address)
                }
                context.startActivity(wakeIntent)
                Log.d(TAG, "✅ 前台直接启动成功: ${device.name}")
            } catch (e: Exception) {
                Log.w(TAG, "前台启动失败: ${e.message}")
            }
        }

        // 方案2: 尝试直接启动Activity
        // 注意：不要用 CLEAR_TASK，会清除整个任务栈导致Activity被销毁
//        val wakeIntent = BluetoothWakeActivity.createIntent(context).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_NAME, device.name)
//            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_ADDRESS, device.address)
//        }
//
//        try {
//            context.startActivity(wakeIntent)
//            Log.d(TAG, "✅ APP已直接启动: ${device.name}")
//            BluetoothWakeReceiver.isLastConnected = true
//        } catch (e: Exception) {
//            Log.w(TAG, "⚠️ 直接启动被阻止，请点击通知打开: ${e.message}")
//            // Android 12+ 后台启动被阻止是正常的，用户需要点击通知
//        }
    }
}
