package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.ai.ClaudeAIService
import com.voicejournal.ui.screens.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AIChatViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = ClaudeAIService(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(content: String) {
        // 添加用户消息
        val userMessage = ChatMessage(content = content, isUser = true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // 构建对话上下文
                val conversationContext = buildConversationContext()

                // 调用AI
                val prompt = """
                    你是一个专业的情绪助手，帮助用户理解和管理情绪。

                    对话历史：
                    $conversationContext

                    用户最新消息：$content

                    请提供温暖、专业的回应，包括：
                    1. 理解和共情
                    2. 情绪分析
                    3. 实用建议
                    4. 鼓励和支持

                    回复要简洁、温暖、有帮助。
                """.trimIndent()

                val response = aiService.generateSummary(prompt, maxLength = 500)

                // 添加AI回复
                val aiMessage = ChatMessage(content = response, isUser = false)
                _messages.value = _messages.value + aiMessage

            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "抱歉，我遇到了一些问题。请稍后再试。",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    private fun buildConversationContext(): String {
        return _messages.value.takeLast(5).joinToString("\n") { message ->
            val role = if (message.isUser) "用户" else "助手"
            "$role: ${message.content}"
        }
    }
}
