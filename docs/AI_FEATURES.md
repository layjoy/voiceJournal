# AI功能说明文档

## 🤖 AI功能概述

声音日记集成了Claude AI大模型，提供深度情绪分析、智能洞察和个性化建议等功能。

## ✨ 核心AI功能

### 1. 深度情绪分析

**功能描述**：
- 分析日记内容的情绪细节
- 识别主要情绪和次要情绪
- 评估情绪强度（0-10分）
- 分析情绪变化趋势
- 识别情绪触发因素
- 计算积极/消极情绪比例

**使用场景**：
- 单条日记的深度分析
- 了解自己的情绪状态
- 追踪情绪变化

**技术实现**：
```kotlin
suspend fun analyzeEmotionDeep(text: String): EmotionAnalysisResult
```

### 2. AI洞察生成

**功能描述**：
- 分析多条日记，提取关键模式
- 识别情绪模式（如：工作日焦虑，周末放松）
- 发现关注主题（如：工作、家庭、健康）
- 识别积极变化
- 标记需要关注的问题
- 提供个性化建议

**使用场景**：
- 定期回顾（每周/每月）
- 了解自己的情绪模式
- 获取改善建议

**技术实现**：
```kotlin
suspend fun generateInsights(entries: List<String>): InsightResult
```

### 3. 写作建议

**功能描述**：
- 评估表达清晰度（0-10分）
- 评估情感表达（0-10分）
- 提供具体改进建议
- 建议可以添加的细节
- 提供更好的表达方式示例

**使用场景**：
- 提升日记写作质量
- 学习更好的表达方式
- 记录更有价值的内容

**技术实现**：
```kotlin
suspend fun getWritingSuggestions(text: String): WritingSuggestionResult
```

### 4. 情绪趋势预测

**功能描述**：
- 基于历史数据预测未来情绪趋势
- 预测未来3天的情绪走向
- 提供预测依据
- 标记潜在风险
- 给出改善建议

**使用场景**：
- 提前预防情绪低谷
- 规划情绪管理策略
- 了解情绪周期

**技术实现**：
```kotlin
suspend fun predictEmotionTrend(recentEntries: List<Pair<String, String>>): TrendPredictionResult
```

### 5. 智能摘要

**功能描述**：
- 为长日记生成简洁摘要
- 抓住核心内容
- 保留情绪色彩
- 一句话概括

**使用场景**：
- 快速浏览历史日记
- 生成日记标题
- 分享日记片段

**技术实现**：
```kotlin
suspend fun generateSummary(text: String, maxLength: Int = 50): String
```

### 6. 相似日记推荐

**功能描述**：
- 找出情绪或主题相似的历史日记
- 计算相似度评分
- 说明相似原因
- 帮助回忆相关经历

**使用场景**：
- 发现情绪模式
- 回顾相似经历
- 对比情绪变化

**技术实现**：
```kotlin
suspend fun findSimilarEntries(
    currentText: String,
    historicalEntries: List<Pair<String, String>>
): List<SimilarEntry>
```

## 🎯 使用指南

### 配置API Key

1. 获取Anthropic API Key：
   - 访问 https://console.anthropic.com/
   - 注册账号
   - 创建API Key

2. 配置到应用：
   ```kotlin
   // ClaudeAIService.kt
   private const val API_KEY = "YOUR_ANTHROPIC_API_KEY"
   ```

### 访问AI功能

#### 方式1：AI洞察页面
1. 在主界面点击右上角的"AI洞察"图标（大脑图标）
2. 查看情绪模式、关注主题、积极变化等
3. 获取个性化建议

#### 方式2：单条日记分析
1. 进入时间轴
2. 点击日记卡片上的"AI分析"按钮（星星图标）
3. 查看深度情绪分析、写作建议、相似日记

## 📊 AI分析结果示例

### 深度情绪分析示例

```json
{
  "primary_emotion": "焦虑",
  "intensity": 7,
  "secondary_emotions": ["担心", "紧张"],
  "trend": "上升",
  "triggers": ["工作压力", "deadline临近"],
  "positive_ratio": 0.3,
  "analysis": "从日记内容来看，你目前处于较高的焦虑状态..."
}
```

### 洞察生成示例

```json
{
  "patterns": [
    "工作日情绪较低，周末情绪回升",
    "晚上记录的日记情绪更消极"
  ],
  "themes": ["工作", "人际关系", "自我成长"],
  "positive_changes": ["运动频率增加", "睡眠质量改善"],
  "concerns": ["工作压力持续增大", "社交活动减少"],
  "suggestions": [
    "建议增加工作日的放松活动",
    "尝试在白天记录日记"
  ]
}
```

## 🔧 技术架构

### AI服务层
```
ClaudeAIService
├── analyzeEmotionDeep()      # 深度情绪分析
├── generateInsights()         # 洞察生成
├── getWritingSuggestions()    # 写作建议
├── predictEmotionTrend()      # 趋势预测
├── generateSummary()          # 智能摘要
└── findSimilarEntries()       # 相似推荐
```

### ViewModel层
```
AIInsightsViewModel           # AI洞察页面
├── insights: StateFlow
├── emotionTrend: StateFlow
└── loadInsights()

AIAnalysisViewModel           # 单条分析页面
├── deepAnalysis: StateFlow
├── writingSuggestions: StateFlow
├── similarEntries: StateFlow
└── analyzeEntry()
```

### UI层
```
AIInsightsScreen              # AI洞察界面
├── EmotionTrendCard
└── InsightCard

AIAnalysisScreen              # 深度分析界面
├── DeepEmotionAnalysisCard
├── WritingSuggestionsCard
└── SimilarEntriesCard
```

## 💡 最佳实践

### 1. 提高分析准确度
- 日记内容尽量详细
- 描述具体事件和感受
- 使用情绪关键词

### 2. 优化API使用
- 批量分析而非频繁调用
- 缓存分析结果
- 控制请求频率

### 3. 隐私保护
- API调用使用HTTPS
- 不存储API响应到本地
- 用户可选择是否启用AI功能

## 🔐 隐私和安全

### 数据处理
- 日记内容仅在分析时发送到Claude API
- 不存储在Anthropic服务器
- 分析结果仅保存在本地

### API安全
- 使用HTTPS加密传输
- API Key存储在应用内
- 支持用户自定义API Key

### 用户控制
- 可选择是否启用AI功能
- 可随时删除分析结果
- 完全控制数据使用

## 📈 性能优化

### 1. 异步处理
```kotlin
viewModelScope.launch {
    val result = aiService.analyzeEmotionDeep(text)
    _analysis.value = result
}
```

### 2. 结果缓存
- 缓存最近的分析结果
- 避免重复分析相同内容
- 设置缓存过期时间

### 3. 错误处理
```kotlin
try {
    val result = callClaudeAPI(prompt)
    parseResult(result)
} catch (e: Exception) {
    EmotionAnalysisResult.error("分析失败")
}
```

## 🚀 未来功能

### v1.1
- [ ] 情绪日历可视化
- [ ] 周报/月报自动生成
- [ ] 语音情绪识别

### v1.2
- [ ] 多人对比分析
- [ ] 情绪预警系统
- [ ] AI对话功能

### v2.0
- [ ] 个性化AI模型
- [ ] 情绪治疗建议
- [ ] 专业心理评估

## 📞 技术支持

### 常见问题

**Q: API调用失败怎么办？**
A: 检查网络连接和API Key配置

**Q: 分析结果不准确？**
A: 提供更详细的日记内容，使用更多情绪关键词

**Q: 如何保护隐私？**
A: 可以选择不启用AI功能，或使用本地情绪分析

### 联系方式
- 查看 TROUBLESHOOTING.md
- 提交 GitHub Issue

---

**AI功能由Claude 3.5 Sonnet驱动**  
**版本**: v1.0.0  
**更新日期**: 2026-04-19
