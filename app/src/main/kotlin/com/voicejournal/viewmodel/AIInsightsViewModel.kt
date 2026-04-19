package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.ai.ClaudeAIService
import com.voicejournal.ai.InsightResult
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AIInsightsViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = ClaudeAIService(application)
    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _insights = MutableStateFlow(InsightResult(
        patterns = emptyList(),
        themes = emptyList(),
        positiveChanges = emptyList(),
        concerns = emptyList(),
        suggestions = emptyList()
    ))
    val insights: StateFlow<InsightResult> = _insights

    private val _emotionTrend = MutableStateFlow<String?>(null)
    val emotionTrend: StateFlow<String?> = _emotionTrend

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadInsights() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // 获取最近的日记
                repository.getAllEntries().collect { entries ->
                    if (entries.isNotEmpty()) {
                        val recentEntries = entries.take(10)
                        val texts = recentEntries.map { it.transcription }

                        // 生成洞察
                        val result = aiService.generateInsights(texts)
                        _insights.value = result

                        // 预测情绪趋势
                        val entriesWithDate = recentEntries.map {
                            formatDate(it.timestamp) to it.transcription
                        }
                        val trendResult = aiService.predictEmotionTrend(entriesWithDate)
                        _emotionTrend.value = trendResult.prediction
                    }
                }
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
        return sdf.format(java.util.Date(timestamp))
    }
}
