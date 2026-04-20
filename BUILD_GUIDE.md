# 🚀 自动构建说明

## GitHub Actions 自动构建

### 触发条件

✅ **自动触发** - 无需手动操作：
- 推送到 main 分支
- 创建 Pull Request
- 创建 Tag

✅ **手动触发**：
- 访问 [Actions 页面](https://github.com/layjoy/voiceJournal/actions)
- 选择 "Android CI" workflow
- 点击 "Run workflow"

### 构建产物

每次构建完成后，自动生成：
- **app-debug.apk** - Debug 版本（保留 30 天）
- **app-release-unsigned.apk** - Release 版本（保留 90 天）

### 下载 APK

1. 访问 https://github.com/layjoy/voiceJournal/actions
2. 点击最新的 ✅ 成功构建
3. 滚动到底部 "Artifacts" 区域
4. 下载 `app-debug` 或 `app-release`

---

## 本地构建（需要 Java 17）

### 安装 Java 17

#### macOS (使用 Homebrew)
```bash
# 安装 Homebrew（如果未安装）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装 Java 17
brew install openjdk@17

# 设置环境变量
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

#### 或下载安装包
访问 https://adoptium.net/temurin/releases/?version=17 下载安装

### 构建 APK

```bash
cd /Users/admin/Projects/voiceJournal

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# APK 位置
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 使用 Android Studio 构建

如果不想安装命令行工具：

1. 下载 [Android Studio](https://developer.android.com/studio)
2. 打开项目: `/Users/admin/Projects/voiceJournal`
3. 点击 **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)**
4. 构建完成后点击通知中的 **locate** 查看 APK

---

## 推荐方式

### ✅ 方式 1: GitHub Actions（推荐）

**优点**：
- ✅ 无需本地环境
- ✅ 自动构建
- ✅ 每次推送自动触发
- ✅ 可下载历史版本

**使用**：
```bash
# 推送代码即可自动构建
git push github main

# 5-10 分钟后访问 Actions 下载 APK
```

### 方式 2: Android Studio

**优点**：
- ✅ 图形界面
- ✅ 包含所有工具
- ✅ 可以调试

**缺点**：
- ❌ 需要下载 ~1GB
- ❌ 首次构建较慢

### 方式 3: 命令行

**优点**：
- ✅ 快速
- ✅ 可脚本化

**缺点**：
- ❌ 需要安装 Java 17

---

## 当前状态

✅ **GitHub Actions 已配置**
- 每次推送自动构建
- 无需手动操作
- APK 自动上传

❌ **本地环境未配置**
- 需要安装 Java 17
- 或使用 Android Studio

---

## 快速获取 APK

**最快方式**：等待 GitHub Actions 构建完成（5-10 分钟）

1. 访问: https://github.com/layjoy/voiceJournal/actions
2. 等待绿色 ✅ 出现
3. 点击进入，下载 Artifacts

**当前构建状态**: 🔄 构建中...

---

**更新时间**: 2026-04-20  
**推荐方式**: GitHub Actions 自动构建
