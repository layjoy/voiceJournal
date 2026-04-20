package com.voicejournal.ai

data class AIConfig(
    val provider: AIProvider = AIProvider.CLAUDE,
    val apiKey: String = "",
    val baseUrl: String = "",
    val model: String = ""
) {
    fun isConfigured(): Boolean {
        return apiKey.isNotEmpty()
    }

    fun getEffectiveBaseUrl(): String {
        return baseUrl.ifEmpty { provider.defaultBaseUrl }
    }

    fun getEffectiveModel(): String {
        return model.ifEmpty { provider.defaultModel }
    }
}

enum class AIProvider(
    val displayName: String,
    val defaultBaseUrl: String,
    val defaultModel: String,
    val models: List<String>
) {
    CLAUDE(
        displayName = "Claude (Anthropic)",
        defaultBaseUrl = "https://api.anthropic.com",
        defaultModel = "claude-3-5-sonnet-20241022",
        models = listOf(
            "claude-3-5-sonnet-20241022",
            "claude-3-5-haiku-20241022",
            "claude-3-opus-20240229"
        )
    ),
    OPENAI(
        displayName = "OpenAI",
        defaultBaseUrl = "https://api.openai.com/v1",
        defaultModel = "gpt-4o",
        models = listOf(
            "gpt-4o",
            "gpt-4o-mini",
            "gpt-4-turbo",
            "gpt-3.5-turbo"
        )
    ),
    DEEPSEEK(
        displayName = "DeepSeek",
        defaultBaseUrl = "https://api.deepseek.com",
        defaultModel = "deepseek-chat",
        models = listOf(
            "deepseek-chat",
            "deepseek-coder"
        )
    ),
    ZHIPU(
        displayName = "智谱 AI (GLM)",
        defaultBaseUrl = "https://open.bigmodel.cn/api/paas/v4",
        defaultModel = "glm-4-plus",
        models = listOf(
            "glm-4-plus",
            "glm-4-air",
            "glm-4-flash"
        )
    ),
    QWEN(
        displayName = "通义千问 (Qwen)",
        defaultBaseUrl = "https://dashscope.aliyuncs.com/api/v1",
        defaultModel = "qwen-plus",
        models = listOf(
            "qwen-plus",
            "qwen-turbo",
            "qwen-max"
        )
    ),
    CUSTOM(
        displayName = "自定义",
        defaultBaseUrl = "",
        defaultModel = "",
        models = emptyList()
    )
}
