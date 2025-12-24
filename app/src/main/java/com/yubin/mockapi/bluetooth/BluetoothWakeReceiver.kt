package com.yubin.mockapi.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 蓝牙设备连接广播接收器
 * 功能：监听蓝牙设备连接状态，当检测到设备连接时唤醒APP
 *
 * @author Claude
 * @date 2025-12-24
 */
class BluetoothWakeReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BluetoothWakeReceiver"

        // 蓝牙连接状态变化广播
        const val ACTION_CONNECTION_STATE_CHANGED =
            "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED"

        // 蓝牙设备连接广播（部分设备使用）
        const val ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED"

        // 保存唤醒状态
        var isLastConnected = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action ?: return

        Log.d(TAG, "收到蓝牙广播: $action")

        when (action) {
            // 蓝牙适配器连接状态变化
            ACTION_CONNECTION_STATE_CHANGED -> {
                handleConnectionStateChanged(context, intent)
            }
            // 蓝牙设备ACL连接
            ACTION_ACL_CONNECTED -> {
                handleDeviceConnected(context, intent)
            }
        }
    }

    /**
     * 处理蓝牙连接状态变化
     */
    private fun handleConnectionStateChanged(context: Context, intent: Intent) {
        // 获取连接状态
        val state = intent.getIntExtra(
            BluetoothAdapter.EXTRA_CONNECTION_STATE,
            BluetoothAdapter.STATE_DISCONNECTED
        )

        // 获取之前的状态
        val prevState = intent.getIntExtra(
            BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE,
            BluetoothAdapter.STATE_DISCONNECTED
        )

        Log.d(TAG, "蓝牙连接状态变化: $prevState -> $state")

        // 当状态从断开变为连接时，唤醒APP
        if (prevState == BluetoothAdapter.STATE_DISCONNECTED &&
            state == BluetoothAdapter.STATE_CONNECTED
        ) {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            device?.let {
                Log.d(TAG, "蓝牙设备已连接: ${it.name} (${it.address})")
                wakeUpApp(context, it)
            }
        }
    }

    /**
     * 处理蓝牙设备连接
     */
    private fun handleDeviceConnected(context: Context, intent: Intent) {
        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        device?.let {
            Log.d(TAG, "ACL蓝牙设备已连接: ${it.name} (${it.address})")
            wakeUpApp(context, it)
        }
    }

    /**
     * 唤醒APP
     * 启动BluetoothWakeActivity并传递设备信息
     */
    private fun wakeUpApp(context: Context, device: BluetoothDevice) {
        // 防止重复唤醒
        if (isLastConnected) {
            Log.d(TAG, "设备已连接，跳过重复唤醒")
            return
        }
        isLastConnected = true

        val wakeIntent = BluetoothWakeActivity.createIntent(context).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_NAME, device.name)
            putExtra(BluetoothWakeActivity.EXTRA_DEVICE_ADDRESS, device.address)
        }

        context.startActivity(wakeIntent)
        Log.d(TAG, "APP已被蓝牙设备唤醒: ${device.name}")
    }

    /**
     * 重置连接状态（在Activity中调用）
     */
    fun resetConnectionState() {
        isLastConnected = false
    }
}

/**
 * 蓝牙适配器常量（兼容性处理）
 */
private object BluetoothAdapter {
    const val EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE"
    const val EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE"
    const val STATE_DISCONNECTED = 0
    const val STATE_CONNECTED = 2
}
