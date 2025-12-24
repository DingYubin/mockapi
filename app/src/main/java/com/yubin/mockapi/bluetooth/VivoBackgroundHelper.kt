package com.yubin.mockapi.bluetooth

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Vivo 手机后台限制处理工具类
 * 用于检测和引导用户设置 vivo 手机的后台权限
 * 
 * @author Claude
 * @date 2025-12-24
 */
object VivoBackgroundHelper {
    private const val TAG = "VivoBackgroundHelper"
    
    /**
     * 检测是否为 vivo 手机
     */
    fun isVivoDevice(): Boolean {
        return try {
            val manufacturer = Build.MANUFACTURER.lowercase()
            manufacturer.contains("vivo") || manufacturer.contains("bbk")
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 检测是否为其他需要特殊处理的国产手机
     */
    fun isChineseDevice(): Boolean {
        return try {
            val manufacturer = Build.MANUFACTURER.lowercase()
            manufacturer.contains("vivo") || 
            manufacturer.contains("oppo") || 
            manufacturer.contains("xiaomi") || 
            manufacturer.contains("huawei") || 
            manufacturer.contains("honor") ||
            manufacturer.contains("meizu") ||
            manufacturer.contains("oneplus") ||
            manufacturer.contains("realme")
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 打开 vivo 手机的后台高耗电设置页面
     */
    fun openVivoBackgroundSettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // 检查是否可以启动
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Log.d(TAG, "已打开 vivo 后台高耗电设置页面")
                true
            } else {
                // 备选方案：打开应用详情页
                openAppDetailsSettings(context)
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "打开 vivo 设置失败: ${e.message}", e)
            // 备选方案：打开应用详情页
            openAppDetailsSettings(context)
            false
        }
    }
    
    /**
     * 打开 vivo 手机的自启动管理页面
     */
    fun openVivoAutoStartSettings(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"
                )
                putExtra("packagename", context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Log.d(TAG, "已打开 vivo 自启动管理页面")
                true
            } else {
                openAppDetailsSettings(context)
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "打开 vivo 自启动设置失败: ${e.message}", e)
            openAppDetailsSettings(context)
            false
        }
    }
    
    /**
     * 打开应用详情设置页面（通用方法）
     */
    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "已打开应用详情设置页面")
        } catch (e: Exception) {
            Log.e(TAG, "打开应用详情设置失败: ${e.message}", e)
        }
    }
    
    /**
     * 打开电池优化设置页面
     */
    fun openBatteryOptimizationSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                context.startActivity(intent)
                Log.d(TAG, "已打开电池优化设置页面")
            }
        } catch (e: Exception) {
            Log.e(TAG, "打开电池优化设置失败: ${e.message}", e)
        }
    }
    
    /**
     * 检查应用是否在电池优化白名单中
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                powerManager.isIgnoringBatteryOptimizations(context.packageName)
            } catch (e: Exception) {
                Log.e(TAG, "检查电池优化状态失败: ${e.message}", e)
                false
            }
        } else {
            true
        }
    }
    
    /**
     * 请求忽略电池优化
     */
    fun requestIgnoreBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    Log.d(TAG, "已请求忽略电池优化")
                }
            } catch (e: Exception) {
                Log.e(TAG, "请求忽略电池优化失败: ${e.message}", e)
            }
        }
    }
    
    /**
     * 获取 vivo 手机设置指南文本
     */
    fun getVivoSettingsGuide(): String {
        return """
            📱 Vivo 手机后台权限设置指南：
            
            1. 自启动管理
               - 打开「i管家」→「应用管理」→「自启动管理」
               - 找到本应用并开启自启动
            
            2. 后台高耗电
               - 打开「设置」→「电池」→「后台高耗电」
               - 找到本应用并开启
            
            3. 后台应用刷新
               - 打开「设置」→「电池」→「后台应用刷新」
               - 找到本应用并开启
            
            4. 通知权限
               - 打开「设置」→「通知与状态栏」→「应用通知管理」
               - 找到本应用并开启所有通知权限
            
            5. 电池优化
               - 打开「设置」→「电池」→「后台耗电管理」
               - 找到本应用并设置为「允许后台高耗电」
            
            ⚠️ 重要：完成以上设置后，请重启应用以确保生效！
        """.trimIndent()
    }
    
    /**
     * 获取通用国产手机设置指南
     */
    fun getChineseDeviceSettingsGuide(): String {
        return """
            📱 国产手机后台权限设置指南：
            
            为了确保蓝牙唤醒功能正常工作，请完成以下设置：
            
            1. 自启动管理
               - 在系统设置中找到「自启动管理」或「开机自启」
               - 找到本应用并开启自启动
            
            2. 后台运行权限
               - 在系统设置中找到「后台运行」或「后台活动」
               - 找到本应用并允许后台运行
            
            3. 电池优化
               - 在系统设置中找到「电池优化」或「省电管理」
               - 找到本应用并设置为「不优化」或「允许后台高耗电」
            
            4. 通知权限
               - 确保应用的通知权限已开启
            
            5. 应用锁定/保护
               - 在系统设置中找到「应用锁定」或「应用保护」
               - 确保本应用未被锁定或限制
            
            ⚠️ 不同品牌手机设置路径可能不同，请根据实际情况调整！
            ⚠️ 完成设置后，请重启应用以确保生效！
        """.trimIndent()
    }
}

