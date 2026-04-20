# 🔍 APK 构建问题检查报告

## 发现的问题 (7个)

### 🔴 严重问题 (必须修复)

#### 1. 缺少应用图标 ❌
**问题**: AndroidManifest.xml 引用 `@mipmap/ic_launcher`，但 mipmap 目录不存在
**影响**: 构建失败或应用无图标
**位置**: `app/src/main/res/mipmap-*/`
**修复**: 需要创建应用图标

#### 2. Emotion 枚举缺少属性 ❌
**问题**: 代码中使用 `emotion.displayName` 和 `entry.tags`，但模型中未定义
**影响**: 编译错误
**位置**: 
- `ui/screens/ExportScreen.kt:71` - `emotion.displayName`
- `ui/screens/ExportScreen.kt:29` - `emotion.displayName`
- `viewmodel/SearchViewModel.kt:76` - `entry.tags`
**修复**: 需要添加缺失的属性

#### 3. CapsuleAlarmReceiver 包名不匹配 ❌
**问题**: AndroidManifest.xml 引用 `.notification.CapsuleAlarmReceiver`，但类定义在 `CapsuleAlarmScheduler.kt` 中
**影响**: 运行时崩溃
**位置**: `AndroidManifest.xml:31`
**修复**: 需要独立文件或修改 Manifest

---

### 🟡 中等问题 (建议修复)

#### 4. ProGuard 规则不完整 ⚠️
**问题**: 缺少 Gson、Room、Retrofit 的混淆规则
**影响**: Release 构建可能崩溃
**位置**: `app/proguard-rules.pro`
**修复**: 需要添加完整的混淆规则

#### 5. 缺少网络安全配置 ⚠️
**问题**: 未配置 `network_security_config.xml`
**影响**: 可能允许不安全的 HTTP 连接
**位置**: `app/src/main/res/xml/`
**修复**: 建议添加网络安全配置

---

### 🟢 轻微问题 (可选修复)

#### 6. 缺少 colors.xml 📝
**问题**: 主题颜色硬编码在代码中
**影响**: 不影响构建，但不符合最佳实践
**位置**: `app/src/main/res/values/`
**修复**: 建议创建 colors.xml

#### 7. 缺少备用资源 📝
**问题**: 没有 drawable-hdpi, drawable-xhdpi 等密度资源
**影响**: 不影响构建，但在不同设备上显示可能不佳
**位置**: `app/src/main/res/`
**修复**: 建议添加多密度资源

---

## 详细修复方案

### 修复 1: 创建应用图标
```bash
需要创建以下目录和文件:
- mipmap-mdpi/ic_launcher.png (48x48)
- mipmap-hdpi/ic_launcher.png (72x72)
- mipmap-xhdpi/ic_launcher.png (96x96)
- mipmap-xxhdpi/ic_launcher.png (144x144)
- mipmap-xxxhdpi/ic_launcher.png (192x192)
```

### 修复 2: 添加缺失的模型属性
```kotlin
// JournalEntry.kt
data class JournalEntry(
    // ... 现有属性
    val tags: List<String> = emptyList()  // 添加
)

enum class Emotion(val color: Long, val displayName: String) {  // 添加 displayName
    HAPPY(0xFFFFD700, "开心"),
    SAD(0xFF4169E1, "难过"),
    CALM(0xFF4ECDC4, "平静"),
    EXCITED(0xFFFF6584, "兴奋"),
    ANXIOUS(0xFF9370DB, "焦虑"),
    NEUTRAL(0xFF808080, "中性")
}
```

### 修复 3: 独立 CapsuleAlarmReceiver
```kotlin
// 创建新文件: notification/CapsuleAlarmReceiver.kt
package com.voicejournal.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CapsuleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 实现逻辑
    }
}
```

### 修复 4: 完整的 ProGuard 规则
```proguard
# 保留所有项目类
-keep class com.voicejournal.** { *; }

# Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
```

### 修复 5: 网络安全配置
```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

然后在 AndroidManifest.xml 中添加:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## 优先级修复顺序

1. **立即修复** (阻止构建):
   - ✅ 修复 Emotion.displayName
   - ✅ 修复 JournalEntry.tags
   - ✅ 创建应用图标
   - ✅ 修复 CapsuleAlarmReceiver

2. **构建前修复** (避免 Release 崩溃):
   - ✅ 完善 ProGuard 规则

3. **可选修复** (提升质量):
   - 添加网络安全配置
   - 创建 colors.xml
   - 添加多密度资源

---

## 检查命令

```bash
# 检查编译错误
./gradlew compileDebugKotlin

# 检查资源错误
./gradlew processDebugResources

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

---

**生成时间**: 2026-04-20  
**检查状态**: 发现 7 个问题  
**严重问题**: 3 个 (必须修复)  
**中等问题**: 2 个 (建议修复)  
**轻微问题**: 2 个 (可选修复)
