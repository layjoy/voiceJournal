# 故障排除指南

## 常见问题

### 1. 应用无法启动

#### 症状
应用闪退或无法打开

#### 可能原因
- 权限未授予
- 讯飞SDK未正确配置
- 设备不兼容

#### 解决方案
```bash
# 查看崩溃日志
adb logcat | grep AndroidRuntime

# 检查权限
adb shell dumpsys package com.voicejournal | grep permission

# 重新安装
adb uninstall com.voicejournal
adb install app-debug.apk
```

### 2. 录音功能不工作

#### 症状
点击录音按钮无反应或录音失败

#### 可能原因
- 录音权限未授予
- 麦克风被其他应用占用
- 存储空间不足

#### 解决方案
1. 检查权限设置
   - 设置 -> 应用 -> 声音日记 -> 权限 -> 麦克风
   
2. 关闭其他使用麦克风的应用

3. 清理存储空间
   ```bash
   # 查看存储空间
   adb shell df /data
   ```

4. 检查日志
   ```bash
   adb logcat | grep AudioRecorder
   ```

### 3. 语音识别失败

#### 症状
录音完成后无法转文字或显示"识别失败"

#### 可能原因
- 网络连接问题
- 讯飞SDK配置错误
- API配额用尽

#### 解决方案
1. 检查网络连接
   ```bash
   adb shell ping -c 4 www.xfyun.cn
   ```

2. 验证SDK配置
   - 检查 APP_ID、API_KEY、API_SECRET
   - 确认SDK文件在 `app/libs/` 目录

3. 查看识别日志
   ```bash
   adb logcat | grep XunfeiSpeech
   ```

4. 检查讯飞控制台配额

### 4. 数据库错误

#### 症状
应用崩溃，日志显示数据库相关错误

#### 可能原因
- 数据库损坏
- 迁移失败
- 磁盘空间不足

#### 解决方案
1. 清除应用数据
   ```bash
   adb shell pm clear com.voicejournal
   ```

2. 检查数据库文件
   ```bash
   adb shell run-as com.voicejournal ls databases/
   ```

3. 导出数据库进行检查
   ```bash
   adb exec-out run-as com.voicejournal cat databases/journal_database > journal.db
   sqlite3 journal.db "PRAGMA integrity_check;"
   ```

### 5. 通知不显示

#### 症状
时光胶囊解锁时没有收到通知

#### 可能原因
- 通知权限未授予
- 系统省电模式
- 通知渠道被禁用

#### 解决方案
1. 检查通知权限
   - 设置 -> 应用 -> 声音日记 -> 通知

2. 关闭省电模式或将应用加入白名单

3. 检查通知渠道
   ```bash
   adb shell dumpsys notification | grep com.voicejournal
   ```

4. 测试通知
   ```bash
   adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
   ```

### 6. UI显示异常

#### 症状
界面布局错误、文字重叠、按钮不可见

#### 可能原因
- 屏幕尺寸不兼容
- 系统字体大小设置过大
- Compose渲染问题

#### 解决方案
1. 检查设备信息
   ```bash
   adb shell wm size
   adb shell wm density
   ```

2. 重置系统字体大小
   - 设置 -> 显示 -> 字体大小 -> 默认

3. 清除应用缓存
   ```bash
   adb shell pm clear com.voicejournal
   ```

### 7. 性能问题

#### 症状
应用卡顿、响应慢、耗电快

#### 可能原因
- 内存泄漏
- 后台任务过多
- 数据库查询未优化

#### 解决方案
1. 使用Android Profiler分析
   - Android Studio -> View -> Tool Windows -> Profiler

2. 检查内存使用
   ```bash
   adb shell dumpsys meminfo com.voicejournal
   ```

3. 检查CPU使用
   ```bash
   adb shell top | grep voicejournal
   ```

4. 优化数据库查询
   - 添加索引
   - 使用分页加载

### 8. 构建失败

#### 症状
Gradle构建报错

#### 常见错误及解决方案

**错误: SDK not found**
```bash
# 设置ANDROID_HOME环境变量
export ANDROID_HOME=$HOME/Library/Android/sdk
```

**错误: Kotlin version mismatch**
```bash
# 清理并重新构建
./gradlew clean
./gradlew build --refresh-dependencies
```

**错误: Room annotation processor**
```bash
# 确保使用KSP而不是kapt
# 检查 build.gradle.kts 中的配置
```

**错误: 讯飞SDK not found**
```bash
# 检查SDK文件位置
ls -la app/libs/
# 应该包含 .jar 或 .aar 文件
```

## 调试工具

### 1. ADB命令

```bash
# 查看设备
adb devices

# 安装应用
adb install app-debug.apk

# 卸载应用
adb uninstall com.voicejournal

# 查看日志
adb logcat

# 清除日志
adb logcat -c

# 过滤日志
adb logcat | grep VoiceJournal

# 截图
adb shell screencap /sdcard/screen.png
adb pull /sdcard/screen.png

# 录屏
adb shell screenrecord /sdcard/demo.mp4
```

### 2. Logcat过滤

```bash
# 按标签过滤
adb logcat -s TAG_NAME

# 按优先级过滤
adb logcat *:E  # 只显示错误

# 按包名过滤
adb logcat | grep com.voicejournal

# 保存到文件
adb logcat > logcat.txt
```

### 3. 性能分析

```bash
# CPU使用率
adb shell top -m 10

# 内存使用
adb shell dumpsys meminfo com.voicejournal

# 电池使用
adb shell dumpsys batterystats com.voicejournal

# 网络使用
adb shell dumpsys netstats detail
```

## 获取帮助

如果以上方法都无法解决问题：

1. 收集日志
   ```bash
   adb logcat > full_log.txt
   ```

2. 记录复现步骤

3. 提交Issue到GitHub仓库，包含：
   - 设备型号和Android版本
   - 应用版本
   - 详细的问题描述
   - 复现步骤
   - 日志文件

## 联系方式

- GitHub Issues: [项目地址]/issues
- Email: [联系邮箱]

## 诊断脚本

创建 `diagnose.sh` 脚本自动收集诊断信息：

```bash
#!/bin/bash
echo "收集诊断信息..."
echo "设备信息:" > diagnose.txt
adb shell getprop ro.product.model >> diagnose.txt
adb shell getprop ro.build.version.release >> diagnose.txt
echo "\n应用信息:" >> diagnose.txt
adb shell dumpsys package com.voicejournal | grep version >> diagnose.txt
echo "\n权限信息:" >> diagnose.txt
adb shell dumpsys package com.voicejournal | grep permission >> diagnose.txt
echo "\n最近日志:" >> diagnose.txt
adb logcat -d | grep -i voicejournal | tail -100 >> diagnose.txt
echo "诊断信息已保存到 diagnose.txt"
```
