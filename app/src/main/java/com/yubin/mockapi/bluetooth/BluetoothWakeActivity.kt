package com.yubin.mockapi.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yubin.mockapi.R
import com.yubin.mockapi.bluetooth.VivoBackgroundHelper

/**
 * 蓝牙唤醒演示页面
 * 功能：展示蓝牙设备连接信息，演示蓝牙唤醒APP功能
 *
 * @author Claude
 * @date 2025-12-24
 */
class BluetoothWakeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BluetoothWakeActivity"

        // Intent传递的Key
        const val EXTRA_DEVICE_NAME = "device_name"
        const val EXTRA_DEVICE_ADDRESS = "device_address"

        // 权限请求码
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 1001
        private const val REQUEST_ENABLE_BLUETOOTH = 1002

        /**
         * 创建启动Intent
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, BluetoothWakeActivity::class.java)
        }
    }

    // UI组件
    private lateinit var tvStatus: TextView
    private lateinit var tvDeviceInfo: TextView
    private lateinit var tvLog: TextView
    private lateinit var scrollLog: ScrollView
    private lateinit var switchService: Switch
    private lateinit var btnClearLog: Button
    private lateinit var btnStartService: Button
    private lateinit var btnStopService: Button

    // 蓝牙适配器
    private var bluetoothAdapter: BluetoothAdapter? = null

    // 日志内容
    private val logContent = StringBuilder()

    // 应用前后台状态标志
    private var isAppInForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_wake)

        initViews()
        handleIntent(intent)
        checkAndRequestPermissions()
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvLog = findViewById(R.id.tvLog)
        scrollLog = findViewById(R.id.scrollLog)
        switchService = findViewById(R.id.switchService)
        btnClearLog = findViewById(R.id.btnClearLog)
        btnStartService = findViewById(R.id.btnStartService)
        btnStopService = findViewById(R.id.btnStopService)

        // 按钮点击事件
        btnClearLog.setOnClickListener {
            logContent.clear()
            tvLog.text = ""
            addLog("日志已清空")
        }

        btnStartService.setOnClickListener {
            startWakeService()
        }

        btnStopService.setOnClickListener {
            stopWakeService()
        }

        switchService.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startWakeService()
            } else {
                stopWakeService()
            }
        }

        // 检查是否是vivo手机，如果是则添加后台权限提示
        checkAndShowVivoBackendHint()

        addLog("页面初始化完成")
        addLog("等待权限授予...")
    }

    /**
     * 检查并显示vivo后台权限提示
     */
    private fun checkAndShowVivoBackendHint() {
        if (VivoBackgroundHelper.isVivoDevice() || VivoBackgroundHelper.isChineseDevice()) {
            addLog("⚠️ 检测到 ${Build.MANUFACTURER} 手机")
            addLog("⚠️ 注意：在后台时可能无法接收蓝牙连接广播")
            addLog("⚠️ 请在设置中开启本应用的后台运行权限")
            addLog("💡 建议：在「设置」→「电池」→「后台高耗电」中开启本应用")
            addLog("💡 建议：在「i管家」→「应用管理」→「自启动管理」中开启本应用")
        }
    }

    /**
     * 检查并请求权限
     */
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        // Android 12+ (API 31+) 需要新的蓝牙权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        // Android 13+ 需要通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isEmpty()) {
            // 权限已授予，初始化蓝牙
            addLog("所有权限已授予")
            initBluetooth()
        } else {
            // 请求权限
            addLog("请求权限: ${neededPermissions.joinToString()}")
            ActivityCompat.requestPermissions(
                this,
                neededPermissions.toTypedArray(),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSIONS -> {
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (allGranted) {
                    addLog("权限已授予")
                    initBluetooth()
                } else {
                    addLog("警告: 部分权限未授予，功能可能受限")
                    updateStatus("权限不足", false)
                    // 权限不足时也初始化蓝牙适配器，但不调用需要权限的API
                    initBluetoothAdapterOnly()
                }
            }
        }
    }

    /**
     * 只初始化蓝牙适配器，不调用需要权限的API
     */
    private fun initBluetoothAdapterOnly() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            updateStatus("设备不支持蓝牙", false)
            addLog("错误: 设备不支持蓝牙")
        } else {
            addLog("蓝牙适配器已初始化（等待完整权限）")
        }
    }

    /**
     * 初始化蓝牙（需要权限）
     */
    private fun initBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            updateStatus("设备不支持蓝牙", false)
            addLog("错误: 设备不支持蓝牙")
            return
        }

        updateBluetoothStatus()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * 处理Intent（来自蓝牙唤醒）
     */
    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
        val deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)

        if (!deviceName.isNullOrEmpty() || !deviceAddress.isNullOrEmpty()) {
            addLog("=".repeat(50))
            addLog("📱 APP已被蓝牙设备唤醒！")
            addLog("设备名称: ${deviceName ?: "未知"}")
            addLog("设备地址: ${deviceAddress ?: "未知"}")
            addLog("=".repeat(50))

            tvDeviceInfo.text = buildDeviceInfo(deviceName, deviceAddress)
        }
    }

    /**
     * 更新蓝牙状态显示（需要权限）
     */
    private fun updateBluetoothStatus() {
        // 再次检查权限，防止Android 12+崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasConnectPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasConnectPermission) {
                updateStatus("等待权限授权", false)
                addLog("等待蓝牙连接权限...")
                return
            }
        }

        val isEnabled = bluetoothAdapter?.isEnabled == true
        updateStatus(if (isEnabled) "蓝牙已开启" else "蓝牙已关闭", isEnabled)

        if (isEnabled) {
            try {
                val bondedDevices = bluetoothAdapter?.bondedDevices
                addLog("已配对设备数量: ${bondedDevices?.size ?: 0}")
                bondedDevices?.forEach { device ->
                    addLog("  - ${device.name} (${device.address})")
                }
            } catch (e: SecurityException) {
                addLog("获取配对设备失败: ${e.message}")
                Log.w(TAG, "获取配对设备失败", e)
            }
        }
    }

    /**
     * 更新状态显示
     */
    private fun updateStatus(text: String, enabled: Boolean) {
        tvStatus.text = text
        tvStatus.setTextColor(
            resources.getColor(
                if (enabled) android.R.color.holo_green_dark
                else android.R.color.holo_red_dark,
                null
            )
        )
    }

    /**
     * 构建设备信息字符串
     */
    private fun buildDeviceInfo(name: String?, address: String?): String {
        return """
            蓝牙设备信息:
            名称: ${name ?: "未知"}
            地址: ${address ?: "未知"}

            ${if (name != null) "✅ APP已通过蓝牙唤醒" else "等待蓝牙设备连接..."}
        """.trimIndent()
    }

    /**
     * 启动唤醒服务
     */
    private fun startWakeService() {
        val intent = Intent(this, BluetoothWakeService::class.java).apply {
            action = BluetoothWakeService.ACTION_START_SERVICE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        addLog("蓝牙唤醒服务已启动")
        switchService.isChecked = true
    }

    /**
     * 停止唤醒服务
     */
    private fun stopWakeService() {
        val intent = Intent(this, BluetoothWakeService::class.java).apply {
            action = BluetoothWakeService.ACTION_STOP_SERVICE
        }
        stopService(intent)
        addLog("蓝牙唤醒服务已停止")
        switchService.isChecked = false
        BluetoothWakeReceiver().resetConnectionState()
    }

    /**
     * 添加日志
     */
    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        logContent.append("[$timestamp] $message\n")
        tvLog.text = logContent.toString()

        // 自动滚动到底部
        scrollLog.post {
            scrollLog.fullScroll(ScrollView.FOCUS_DOWN)
        }

        Log.d(TAG, message)
    }

    override fun onStart() {
        super.onStart()
        // 应用从后台切到前台
        if (!isAppInForeground) {
            isAppInForeground = true
            addLog("🔄 APP切换到前台")
        }
    }

    override fun onResume() {
        super.onResume()
        // 只有权限授予后才更新蓝牙状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                updateBluetoothStatus()
            }
        } else {
            updateBluetoothStatus()
        }
    }

    override fun onStop() {
        super.onStop()
        // 应用切到后台
        if (isAppInForeground) {
            isAppInForeground = false
            addLog("🔄 APP切换到后台")
            // 如果是vivo等国产手机，添加提醒
            if (VivoBackgroundHelper.isVivoDevice() || VivoBackgroundHelper.isChineseDevice()) {
                addLog("⚠️ 后台模式下，vivo等手机可能限制接收蓝牙广播")
                addLog("⚠️ 如需后台唤醒功能，请设置后台权限")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addLog("页面销毁")
        // 页面销毁时重置前台状态
        isAppInForeground = false
    }
}
