# 开发指南

## 开发环境设置

### 必需工具
- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK 34
- Gradle 8.2

### 推荐插件
- Kotlin
- Compose Multiplatform IDE Support
- Rainbow Brackets
- GitToolBox

## 项目架构

### MVVM架构

```
UI Layer (Compose)
    ↓
ViewModel Layer
    ↓
Repository Layer
    ↓
Data Source Layer (Room, Audio, Speech)
```

### 数据流

```
User Action → UI → ViewModel → Repository → Data Source
                ↑                              ↓
                ←←←←←← StateFlow ←←←←←←←←←←←←←←
```

## 代码规范

### Kotlin编码规范

遵循 [Kotlin官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)

#### 命名规范
- 类名: PascalCase (`RecordViewModel`)
- 函数名: camelCase (`startRecording`)
- 常量: UPPER_SNAKE_CASE (`APP_ID`)
- 私有属性: 下划线前缀 (`_isRecording`)

#### 代码组织
```kotlin
class MyClass {
    // 1. 伴生对象
    companion object { }
    
    // 2. 属性
    private val _state = MutableStateFlow()
    val state: StateFlow = _state
    
    // 3. 初始化块
    init { }
    
    // 4. 公共方法
    fun publicMethod() { }
    
    // 5. 私有方法
    private fun privateMethod() { }
}
```

### Compose最佳实践

#### 1. 状态提升
```kotlin
// ❌ 不好
@Composable
fun MyScreen() {
    var state by remember { mutableStateOf("") }
    // 状态在Composable内部
}

// ✅ 好
@Composable
fun MyScreen(
    state: String,
    onStateChange: (String) -> Unit
) {
    // 状态由外部管理
}
```

#### 2. 副作用处理
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(key1 = Unit) {
        // 一次性副作用
    }
    
    DisposableEffect(key1 = Unit) {
        // 需要清理的副作用
        onDispose { }
    }
}
```

#### 3. 性能优化
```kotlin
// 使用 remember 缓存计算结果
val expensiveValue = remember(key) {
    computeExpensiveValue()
}

// 使用 derivedStateOf 避免不必要的重组
val derivedValue by remember {
    derivedStateOf { state.value * 2 }
}
```

## 添加新功能

### 1. 创建数据模型

```kotlin
// data/model/NewFeature.kt
data class NewFeature(
    val id: String,
    val name: String
)
```

### 2. 创建数据库实体（如需持久化）

```kotlin
// data/database/NewFeatureEntity.kt
@Entity(tableName = "new_features")
data class NewFeatureEntity(
    @PrimaryKey val id: String,
    val name: String
)
```

### 3. 创建DAO

```kotlin
// data/database/NewFeatureDao.kt
@Dao
interface NewFeatureDao {
    @Query("SELECT * FROM new_features")
    fun getAll(): Flow<List<NewFeatureEntity>>
    
    @Insert
    suspend fun insert(feature: NewFeatureEntity)
}
```

### 4. 创建Repository

```kotlin
// data/repository/NewFeatureRepository.kt
class NewFeatureRepository(private val dao: NewFeatureDao) {
    fun getAll() = dao.getAll()
    suspend fun insert(feature: NewFeature) {
        dao.insert(feature.toEntity())
    }
}
```

### 5. 创建ViewModel

```kotlin
// viewmodel/NewFeatureViewModel.kt
class NewFeatureViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: NewFeatureRepository
    
    val features: StateFlow<List<NewFeature>>
    
    fun addFeature(feature: NewFeature) {
        viewModelScope.launch {
            repository.insert(feature)
        }
    }
}
```

### 6. 创建UI

```kotlin
// ui/screens/NewFeatureScreen.kt
@Composable
fun NewFeatureScreen(
    viewModel: NewFeatureViewModel,
    onNavigateBack: () -> Unit
) {
    val features by viewModel.features.collectAsStateWithLifecycle()
    
    // UI实现
}
```

### 7. 添加导航

```kotlin
// VoiceJournalApp.kt
composable("newFeature") {
    val viewModel: NewFeatureViewModel = viewModel()
    NewFeatureScreen(
        viewModel = viewModel,
        onNavigateBack = { navController.popBackStack() }
    )
}
```

## 测试

### 单元测试

```kotlin
// app/src/test/kotlin/com/voicejournal/MyTest.kt
class MyTest {
    @Test
    fun testSomething() {
        // Arrange
        val input = "test"
        
        // Act
        val result = myFunction(input)
        
        // Assert
        assertEquals("expected", result)
    }
}
```

### UI测试

```kotlin
// app/src/androidTest/kotlin/com/voicejournal/MyUITest.kt
@RunWith(AndroidJUnit4::class)
class MyUITest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testUI() {
        composeTestRule.setContent {
            MyScreen()
        }
        
        composeTestRule
            .onNodeWithText("Button")
            .performClick()
    }
}
```

## 调试技巧

### 1. Logcat过滤

```kotlin
private const val TAG = "MyClass"
Log.d(TAG, "Debug message")
```

在Logcat中过滤: `tag:MyClass`

### 2. Compose布局检查器

Tools -> Layout Inspector -> 选择运行中的应用

### 3. 数据库检查

使用 Database Inspector 查看Room数据库：
View -> Tool Windows -> App Inspection -> Database Inspector

### 4. 性能分析

Android Studio Profiler:
- CPU Profiler: 分析方法执行时间
- Memory Profiler: 检测内存泄漏
- Network Profiler: 监控网络请求

## 常见问题

### 1. Compose重组过多

使用 `remember` 和 `derivedStateOf` 优化。

### 2. 协程泄漏

确保在 `viewModelScope` 中启动协程。

### 3. Room数据库迁移

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ...")
    }
}
```

### 4. 权限被拒绝

检查 `MainActivity` 中的权限请求逻辑。

## Git工作流

### 分支策略

- `main`: 稳定版本
- `develop`: 开发分支
- `feature/*`: 功能分支
- `bugfix/*`: 修复分支

### 提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

类型:
- feat: 新功能
- fix: 修复bug
- docs: 文档
- style: 格式
- refactor: 重构
- test: 测试
- chore: 构建/工具

示例:
```
feat(record): 添加暂停录音功能

- 添加暂停按钮
- 实现暂停/恢复逻辑
- 更新UI状态

Closes #123
```

## 代码审查清单

- [ ] 代码符合Kotlin规范
- [ ] 没有硬编码字符串（使用strings.xml）
- [ ] 没有内存泄漏
- [ ] 正确处理生命周期
- [ ] 添加了必要的注释
- [ ] 更新了文档
- [ ] 添加了测试
- [ ] 测试通过

## 资源

- [Kotlin文档](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose文档](https://developer.android.com/jetpack/compose)
- [Android开发者指南](https://developer.android.com/guide)
- [Material Design 3](https://m3.material.io/)
