# 声音日记 - CI/CD 配置说明

## Gitee Go (推荐)

Gitee 使用 Gitee Go 进行 CI/CD，配置文件为 `.gitlab-ci.yml`

### 启用步骤

1. 访问 Gitee 仓库页面
2. 点击 "服务" -> "Gitee Go"
3. 启用 Gitee Go
4. 推送代码后自动触发构建

### 构建产物

构建完成后，可以在 Gitee Go 页面下载：
- `app-debug.apk` - Debug 版本
- `app-release-unsigned.apk` - Release 版本（未签名）

---

## GitHub Actions (备用)

如果使用 GitHub，配置文件为 `.github/workflows/android.yml`

### 启用步骤

1. 推送代码到 GitHub
2. 自动触发 Actions
3. 在 Actions 页面查看构建状态

---

## 本地构建

如果 CI/CD 不可用，可以本地构建：

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 输出位置
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 签名 Release APK

Release APK 需要签名才能安装：

```bash
# 1. 生成密钥库（首次）
./generate-keystore.sh

# 2. 签名 APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore voicejournal.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  voicejournal

# 3. 对齐 APK
zipalign -v 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app/build/outputs/apk/release/app-release.apk
```

---

## 自动签名配置

在 `app/build.gradle.kts` 中配置：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../voicejournal.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "voicejournal"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

然后在 CI/CD 中设置环境变量：
- `KEYSTORE_PASSWORD`
- `KEY_PASSWORD`

---

## 构建状态

查看构建状态：
- Gitee Go: https://gitee.com/layjoy/voiceJournal/pipelines
- GitHub Actions: https://github.com/layjoy/voiceJournal/actions

---

**生成时间**: 2026-04-20  
**CI/CD 状态**: ✅ 已配置
