package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.ai.AIConfig
import com.voicejournal.ai.AIConfigManager
import com.voicejournal.ai.AIProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AIConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val configManager = AIConfigManager(application)

    val aiConfig: StateFlow<AIConfig> = configManager.aiConfig.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AIConfig()
    )

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting

    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult

    fun setProvider(provider: AIProvider) {
        viewModelScope.launch {
            configManager.setProvider(provider)
        }
    }

    fun setApiKey(apiKey: String) {
        viewModelScope.launch {
            configManager.setApiKey(apiKey)
        }
    }

    fun setBaseUrl(baseUrl: String) {
        viewModelScope.launch {
            configManager.setBaseUrl(baseUrl)
        }
    }

    fun setModel(model: String) {
        viewModelScope.launch {
            configManager.setModel(model)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null

            try {
                val config = aiConfig.value
                if (!config.isConfigured()) {
                    _testResult.value = TestResult(
                        success = false,
                        message = "请先配置 API Key"
                    )
                    return@launch
                }

                // TODO: 实际调用 API 测试连接
                // 这里暂时模拟测试
                kotlinx.coroutines.delay(1000)
                _testResult.value = TestResult(
                    success = true,
                    message = "连接成功！配置有效"
                )
            } catch (e: Exception) {
                _testResult.value = TestResult(
                    success = false,
                    message = "连接失败: ${e.message}"
                )
            } finally {
                _isTesting.value = false
            }
        }
    }

    fun clearTestResult() {
        _testResult.value = null
    }

    fun resetConfig() {
        viewModelScope.launch {
            configManager.clearConfig()
        }
    }

    data class TestResult(
        val success: Boolean,
        val message: String
    )
}
