# 蓝牙唤醒APP功能说明文档

## 项目概述

本 demo 实现了**蓝牙设备连接时自动唤醒 APP** 的功能，用于验证技术可行性。

**创建时间**: 2025-12-24
**状态**: ✅ 编译通过，待真机测试

---

## 功能说明

当蓝牙耳机/设备连接到手机时，APP 会自动启动并显示设备信息。

### 核心流程

```
蓝牙设备连接 → BroadcastReceiver 监听 → 启动 BluetoothWakeActivity → 显示设备信息
```

---

## 文件结构

```
app/src/main/java/com/yubin/mockapi/bluetooth/
├── BluetoothWakeReceiver.kt      # 蓝牙广播接收器（监听连接）
├── BluetoothWakeService.kt       # 后台监听服务
└── BluetoothWakeActivity.kt      # 演示页面

app/src/main/res/layout/
└── activity_bluetooth_wake.xml   # 演示页面布局
```

---

## 代码实现要点

### 1. 权限配置 (AndroidManifest.xml)

```xml
<!-- 蓝牙基础权限 -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

<!-- Android 12+ 需要的新权限 -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

<!-- Android 13+ 通知权限 -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### 2. 监听的蓝牙广播

- `BluetoothDevice.ACTION_ACL_CONNECTED` - 设备已连接
- `BluetoothAdapter.ACTION_STATE_CHANGED` - 蓝牙适配器状态变化

### 3. Android 版本兼容性

| Android 版本 | 需要的权限 | 特殊处理 |
|-------------|----------|---------|
| < 12 | BLUETOOTH, BLUETOOTH_ADMIN | 正常 |
| 12+ (API 31) | BLUETOOTH_CONNECT, BLUETOOTH_SCAN | 运行时权限 |
| 14+ (API 34) | 同上 + foregroundServiceType | 前台服务类型 |

---

## 使用方法

### 方式1: 从 MainActivity 进入

1. 启动 APP
2. 点击主页面上的 **"蓝牙唤醒APP演示"** 按钮
3. 在演示页面点击 **"启动服务"**
4. 连接蓝牙设备，APP 会被唤醒

### 方式2: 直接启动演示页面

```kotlin
startActivity(Intent(context, BluetoothWakeActivity::class.java))
```

---

## 测试步骤

1. **安装 APP 到手机**
   ```bash
   JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.12.jdk/Contents/Home \
   ./gradlew :app:installDebug
   ```

2. **授予蓝牙权限**
   - 首次进入演示页面会请求权限
   - 需要授予：蓝牙连接、蓝牙扫描、通知权限

3. **启动后台监听服务**
   - 打开演示页面
   - 点击"启动服务"按钮

4. **连接蓝牙设备测试**
   - 开启蓝牙耳机/设备
   - 观察 APP 是否被唤醒

---

## 技术可行性验证

| 验证项 | 状态 | 说明 |
|-------|-----|------|
| 编译通过 | ✅ | 代码无错误 |
| 权限配置 | ✅ | 已配置各版本所需权限 |
| 广播监听 | ✅ | 已实现双重监听机制 |
| 后台服务 | ✅ | START_STICKY 保证服务存活 |
| 真机测试 | ⏳ | 待验证 |

---

## 已知限制

1. **Android 14+ 后台启动限制**
   - 系统可能阻止后台启动 Activity
   - 需要使用通知或其他方式提示用户

2. **省电模式影响**
   - 部分手机省电模式会杀死后台服务
   - 建议引导用户将 APP 加入白名单

3. **首次连接延迟**
   - 某些设备首次配对时可能不会触发广播
   - 二次连接通常正常

4. **⚠️ 国产手机后台广播限制（重要）**
   - **vivo、OPPO、小米、华为等国产手机在后台时可能限制 BroadcastReceiver 接收广播**
   - 即使使用前台服务，系统仍可能阻止后台接收蓝牙连接广播
   - **静态注册的 BroadcastReceiver 也可能被系统限制**
   - **这是系统层面的限制，无法通过代码完全解决**

### 解决方案（针对国产手机）

#### 方案1: 用户设置后台权限（推荐）
引导用户在系统设置中开启以下权限：

**vivo 手机：**
- 打开「i管家」→「应用管理」→「自启动管理」→ 开启本应用
- 打开「设置」→「电池」→「后台高耗电」→ 开启本应用
- 打开「设置」→「电池」→「后台应用刷新」→ 开启本应用

**OPPO/一加/Realme：**
- 打开「设置」→「电池」→「应用耗电管理」→ 找到本应用 → 允许后台运行
- 打开「设置」→「应用管理」→「应用列表」→ 找到本应用 → 开启「自启动」

**小米：**
- 打开「设置」→「应用设置」→「授权管理」→「自启动管理」→ 开启本应用
- 打开「设置」→「省电优化」→「应用智能省电」→ 找到本应用 → 选择「无限制」

**华为/荣耀：**
- 打开「设置」→「电池」→「启动管理」→ 找到本应用 → 关闭「自动管理」→ 开启「手动管理」下的所有选项

#### 方案2: 定期轮询检查（备选）
如果广播无法接收，可以考虑定期检查已连接设备列表，但此方法会增加耗电。

#### 方案3: 接受限制，前台唤醒
- 如果应用在前台，可以正常接收广播
- 如果应用在后台，用户需要手动打开应用后才能接收后续的连接事件

---

## 下一步优化建议

1. **添加通知栏提示** - 后台服务显示前台通知
2. **白名单引导** - 引导用户将 APP 加入省电白名单
3. **设备过滤** - 只响应特定设备（如指定 MAC 地址）
4. **历史记录** - 保存唤醒历史记录

---

## 编译命令

```bash
# 设置 Java 11（项目要求）
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.12.jdk/Contents/Home

# 编译 app 模块
./gradlew :app:compileDebugKotlin

# 安装到设备
./gradlew :app:installDebug
```

---

## 作者

Claude (AI Assistant)
