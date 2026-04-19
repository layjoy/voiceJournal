# 发布指南

## 生成签名密钥

### 1. 创建Keystore

```bash
keytool -genkey -v -keystore voicejournal.keystore -alias voicejournal -keyalg RSA -keysize 2048 -validity 10000
```

按提示输入：
- 密钥库密码（建议使用强密码）
- 姓名、组织等信息
- 密钥密码（可以与密钥库密码相同）

### 2. 配置签名

在项目根目录创建 `keystore.properties` 文件（不要提交到Git）：

```properties
storePassword=你的密钥库密码
keyPassword=你的密钥密码
keyAlias=voicejournal
storeFile=../voicejournal.keystore
```

### 3. 更新 build.gradle.kts

在 `app/build.gradle.kts` 中添加签名配置（已配置）：

```kotlin
android {
    signingConfigs {
        create("release") {
            // 从 keystore.properties 读取
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## 构建Release版本

### 方式1: 使用构建脚本

```bash
./build.sh
```

### 方式2: 使用Gradle命令

```bash
# 构建Release APK
./gradlew assembleRelease

# 构建Release AAB (Google Play)
./gradlew bundleRelease
```

### 方式3: 使用Android Studio

1. Build -> Generate Signed Bundle / APK
2. 选择 APK 或 Android App Bundle
3. 选择或创建密钥库
4. 选择 release 构建类型
5. 点击 Finish

## 输出文件位置

构建完成后，文件位置：

- **APK**: `app/build/outputs/apk/release/app-release.apk`
- **AAB**: `app/build/outputs/bundle/release/app-release.aab`

## 版本管理

在 `app/build.gradle.kts` 中更新版本：

```kotlin
defaultConfig {
    versionCode = 1      // 每次发布递增
    versionName = "1.0"  // 显示给用户的版本号
}
```

## 发布前检查清单

- [ ] 所有测试通过
- [ ] 更新版本号
- [ ] 检查权限配置
- [ ] 测试Release版本
- [ ] 准备应用图标
- [ ] 准备应用截图
- [ ] 编写更新日志
- [ ] 检查混淆规则

## 应用商店发布

### Google Play

1. 创建开发者账号（一次性费用 $25）
2. 创建应用
3. 上传 AAB 文件
4. 填写应用信息
5. 设置定价和分发
6. 提交审核

### 其他应用商店

- 华为应用市场
- 小米应用商店
- OPPO软件商店
- vivo应用商店
- 应用宝（腾讯）
- 豌豆荚

每个商店都有自己的开发者注册和应用提交流程。

## 混淆配置

Release版本已启用代码混淆和资源压缩：

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(...)
    }
}
```

混淆规则在 `app/proguard-rules.pro` 中配置。

## 测试Release版本

```bash
# 安装Release APK到设备
adb install app/build/outputs/apk/release/app-release.apk

# 查看日志
adb logcat | grep VoiceJournal
```

## 常见问题

### 1. 签名错误

确保 `keystore.properties` 文件存在且配置正确。

### 2. 混淆导致崩溃

检查 `proguard-rules.pro`，添加必要的 keep 规则。

### 3. 讯飞SDK问题

确保 SDK 文件正确放置在 `app/libs/` 目录。

## 更新发布

1. 修复bug或添加新功能
2. 更新 versionCode 和 versionName
3. 运行测试
4. 构建新版本
5. 上传到应用商店
6. 编写更新日志

## 安全建议

- 不要将 keystore 文件提交到版本控制
- 不要将 keystore.properties 提交到版本控制
- 妥善保管密钥库密码
- 定期备份 keystore 文件
- 使用强密码

## 性能优化

Release版本已启用：
- 代码混淆（减小APK大小）
- 资源压缩（移除未使用资源）
- ProGuard优化

APK大小预估：15-25 MB（含讯飞SDK）
