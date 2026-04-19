# 快速开始指南

## 🚀 5分钟快速上手

### 前置要求

- ✅ Android Studio Hedgehog (2023.1.1+)
- ✅ JDK 17
- ✅ Android设备或模拟器 (API 26+)

### 步骤1: 克隆项目

```bash
cd /Users/admin/Projects/voiceJournal
```

### 步骤2: 配置讯飞SDK

1. 访问 [讯飞开放平台](https://www.xfyun.cn/)
2. 下载"语音听写"SDK
3. 将SDK文件放到 `app/libs/` 目录：

```
app/libs/
├── Msc.jar (或 iflytek-msc-xxx.aar)
└── jniLibs/
    ├── arm64-v8a/libmsc.so
    └── armeabi-v7a/libmsc.so
```

**注意**：SDK配置信息已在代码中设置，无需修改。

### 步骤3: 打开项目

```bash
# 使用Android Studio打开
open -a "Android Studio" /Users/admin/Projects/voiceJournal
```

或者：
1. 打开Android Studio
2. File -> Open
3. 选择 `/Users/admin/Projects/voiceJournal`

### 步骤4: 同步Gradle

等待Gradle自动同步依赖（首次可能需要几分钟）

### 步骤5: 运行应用

1. 连接Android设备或启动模拟器
2. 点击 Run 按钮 (▶️) 或按 `Shift + F10`
3. 选择目标设备
4. 等待应用安装并启动

### 步骤6: 授予权限

首次运行时，应用会请求以下权限：
- 📱 录音权限
- 🔔 通知权限 (Android 13+)
- 💾 存储权限 (Android 9及以下)

点击"允许"即可。

## 🎯 快速测试

### 测试录音功能

1. 点击主界面的紫色麦克风按钮
2. 开始说话，观察声波动画
3. 再次点击停止录音
4. 等待AI识别转文字
5. 查看情绪分析结果
6. 点击"保存日记"

### 测试时间轴

1. 点击右上角的列表图标
2. 查看已保存的日记
3. 点击日记卡片展开详情
4. 点击删除按钮测试删除功能

### 测试时光胶囊

1. 录制一条日记
2. 点击"时光胶囊"按钮
3. 选择解锁时间（建议选7天测试）
4. 点击"创建"
5. 进入时光胶囊页面查看

## 🛠️ 命令行构建

如果不使用Android Studio，可以使用命令行：

```bash
# 进入项目目录
cd /Users/admin/Projects/voiceJournal

# 构建Debug版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 运行测试
./gradlew test

# 使用构建脚本（推荐）
./build.sh
```

## 📱 APK位置

构建完成后，APK文件位于：

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

## 🐛 常见问题

### 问题1: Gradle同步失败

**解决方案**：
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### 问题2: 讯飞SDK not found

**解决方案**：
确保SDK文件在正确位置：
```bash
ls -la app/libs/
```

### 问题3: 权限被拒绝

**解决方案**：
在设备设置中手动授予权限：
设置 -> 应用 -> 声音日记 -> 权限

### 问题4: 录音无声音

**解决方案**：
- 检查麦克风权限
- 关闭其他使用麦克风的应用
- 在真机上测试（模拟器可能不支持）

### 问题5: 语音识别失败

**解决方案**：
- 检查网络连接
- 确认SDK配置正确
- 查看Logcat日志

## 📚 更多文档

- [README.md](README.md) - 完整项目介绍
- [DEVELOPMENT.md](DEVELOPMENT.md) - 开发指南
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - 故障排除
- [RELEASE.md](RELEASE.md) - 发布指南

## 🎨 功能演示

### 主界面
- 深色渐变背景
- 粒子动画效果
- 3D声波可视化
- 脉冲录音按钮

### 时间轴
- 日记列表
- 展开/收起动画
- 情绪标签
- 删除确认

### 时光胶囊
- 锁定状态动画
- 倒计时显示
- 解锁提醒
- 光晕效果

## 💡 使用技巧

1. **录音时长**：建议每条日记30秒-2分钟
2. **语音识别**：说话清晰，避免环境噪音
3. **情绪分析**：使用情绪关键词提高准确率
4. **时光胶囊**：设置有意义的解锁时间
5. **定期备份**：导出重要日记（未来版本）

## 🔧 开发模式

### 启用调试日志

在 `MainActivity.kt` 中添加：
```kotlin
Log.setLevel(Log.DEBUG)
```

### 查看数据库

使用Android Studio的Database Inspector：
View -> Tool Windows -> App Inspection -> Database Inspector

### 性能分析

使用Android Profiler：
View -> Tool Windows -> Profiler

## 📞 获取帮助

遇到问题？
1. 查看 [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
2. 查看项目Issues
3. 提交新Issue

## ✅ 检查清单

开始开发前，确保：
- [ ] Android Studio已安装
- [ ] JDK 17已配置
- [ ] 讯飞SDK已放置
- [ ] Gradle同步成功
- [ ] 设备已连接
- [ ] 权限已授予

## 🎉 开始使用

现在你已经准备好了！开始录制你的第一条声音日记吧！

---

**预计完成时间**：5-10分钟
**难度等级**：⭐⭐ (简单)
**推荐设备**：真机 (模拟器录音可能不稳定)
