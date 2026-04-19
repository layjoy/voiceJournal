# 声音日记 (Voice Journal)

一款用声音记录生活的Android应用，支持AI语音识别、情绪分析和时光胶囊功能。

## ✨ 功能特性

### 核心功能
- 🎙️ **声音录制** - 高质量音频录制，支持实时波形可视化
- 🌊 **3D声波动画** - 根据音量动态调整的声波可视化效果
- 📝 **AI语音识别** - 集成讯飞语音识别，自动转文字
- 😊 **情绪分析** - 基于文本内容智能分析情绪，自动配色
- 🤖 **Claude AI增强** - 深度情绪分析、智能洞察、写作建议
- ⏰ **时光胶囊** - 设置解锁时间，定时查看过去的记忆
- 📅 **时间轴回顾** - 按时间顺序浏览所有日记

### AI功能 (NEW!)
- 🧠 **深度情绪分析** - 多维度情绪分析，识别触发因素
- 📊 **智能洞察** - 发现情绪模式，提供个性化建议
- ✍️ **写作建议** - 提升日记质量，改善表达方式
- 📈 **趋势预测** - 预测未来情绪走向，提前预防
- 🔍 **相似推荐** - 找出相似的历史日记
- 📝 **智能摘要** - 一句话概括日记内容

### 视觉特色
- 深色主题设计，氛围感十足
- 情绪配色系统（开心/难过/平静/兴奋/焦虑/平和）
- 流畅的动画效果
- Material Design 3 设计规范

## 🛠️ 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Repository Pattern
- **数据库**: Room Database
- **异步处理**: Kotlin Coroutines + Flow
- **语音识别**: 讯飞语音SDK
- **导航**: Navigation Compose
- **依赖注入**: ViewModel

## 📦 项目结构

```
app/src/main/kotlin/com/voicejournal/
├── MainActivity.kt                 # 主Activity，权限管理
├── VoiceJournalApp.kt             # 应用导航配置
├── audio/
│   ├── AudioRecorder.kt           # 录音管理
│   └── AudioPlayer.kt             # 音频播放
├── data/
│   ├── model/
│   │   └── JournalEntry.kt        # 日记数据模型
│   ├── database/
│   │   ├── JournalDatabase.kt     # Room数据库
│   │   ├── JournalDao.kt          # 数据访问对象
│   │   └── JournalEntryEntity.kt  # 数据库实体
│   └── repository/
│       └── JournalRepository.kt   # 数据仓库
├── emotion/
│   └── EmotionAnalyzer.kt         # 情绪分析引擎
├── notification/
│   ├── CapsuleNotificationManager.kt  # 通知管理
│   └── CapsuleAlarmScheduler.kt       # 定时任务
├── speech/
│   └── XunfeiSpeechRecognizer.kt  # 讯飞语音识别
├── ui/
│   ├── screens/
│   │   ├── RecordScreen.kt        # 录音界面
│   │   ├── TimelineScreen.kt      # 时间轴界面
│   │   └── CapsuleScreen.kt       # 时光胶囊界面
│   └── theme/
│       ├── Theme.kt               # 主题配置
│       └── Type.kt                # 字体配置
└── viewmodel/
    ├── RecordViewModel.kt         # 录音ViewModel
    ├── TimelineViewModel.kt       # 时间轴ViewModel
    └── CapsuleViewModel.kt        # 胶囊ViewModel
```

## 🚀 构建说明

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2

### 讯飞SDK配置

1. 访问 [讯飞开放平台](https://www.xfyun.cn/)
2. 下载"语音听写"SDK for Android
3. 将SDK文件放到 `app/libs/` 目录：
   ```
   app/libs/
   ├── Msc.jar (或 iflytek-msc-xxx.aar)
   └── jniLibs/
       ├── arm64-v8a/libmsc.so
       └── armeabi-v7a/libmsc.so
   ```

详细配置请参考 [XUNFEI_SDK_SETUP.md](XUNFEI_SDK_SETUP.md)

### 构建步骤

#### 方式1: 使用Android Studio
1. 打开Android Studio
2. File -> Open -> 选择项目目录
3. 等待Gradle同步完成
4. 点击 Run 按钮或使用快捷键 Shift+F10

#### 方式2: 使用命令行
```bash
# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 运行测试
./gradlew test

# 使用构建脚本（推荐）
./build.sh
```

### 运行要求
- 最低Android版本: Android 8.0 (API 26)
- 目标Android版本: Android 14 (API 34)
- 需要权限:
  - 录音权限 (RECORD_AUDIO)
  - 通知权限 (POST_NOTIFICATIONS, Android 13+)
  - 存储权限 (WRITE_EXTERNAL_STORAGE, Android 9及以下)

## 🧪 测试

### 单元测试
```bash
./gradlew test
```

测试覆盖：
- 情绪分析算法测试
- 数据模型测试

### UI测试
```bash
./gradlew connectedAndroidTest
```

测试覆盖：
- 应用启动测试
- 导航功能测试
- UI组件存在性测试

## 📱 使用说明

### 录制日记
1. 点击主界面的麦克风按钮开始录音
2. 实时查看声波动画
3. 再次点击停止录音
4. 等待AI识别转文字和情绪分析
5. 点击"保存日记"按钮

### 查看时间轴
1. 点击右上角列表图标
2. 浏览所有历史日记
3. 点击删除按钮可删除日记

### 创建时光胶囊
1. 录制完成后选择"保存为时光胶囊"
2. 设置解锁时间
3. 到期后会收到通知提醒

## 🎨 设计理念

### 配色方案
- 背景: 深蓝渐变 (#0A0E27 → #1A1F3A)
- 主色: 紫色 (#6C63FF)
- 辅助色: 青色 (#4ECDC4)、粉色 (#FF6584)
- 情绪色:
  - 开心: 金色 (#FFD700)
  - 难过: 蓝色 (#4169E1)
  - 平静: 青色 (#4ECDC4)
  - 兴奋: 粉色 (#FF6584)
  - 焦虑: 紫色 (#9370DB)
  - 平和: 灰色 (#808080)

### 动画设计
- 声波动画: 2秒循环，线性缓动
- 录音按钮: 大小动画，300ms缓动
- 页面切换: 标准Material过渡

## 🔧 配置文件

### 讯飞语音配置
```kotlin
APP_ID: "5ea2c189"
API_KEY: "a36cfb9b3b6e9ddd87212d7b106a82cb"
API_SECRET: "a21702133210bff60dccac53d7d1208a"
```

### 数据库配置
- 数据库名: journal_database
- 版本: 1
- 表名: journal_entries

## 📄 许可证

本项目仅供学习和个人使用。

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📞 联系方式

如有问题或建议，请提交Issue。

---

**注意**: 首次运行需要在真机上测试，模拟器可能无法正常使用录音功能。
