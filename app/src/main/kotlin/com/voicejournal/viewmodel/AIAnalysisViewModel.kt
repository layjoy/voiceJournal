package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.ai.ClaudeAIService
import com.voicejournal.ai.EmotionAnalysisResult
import com.voicejournal.ai.SimilarEntry
import com.voicejournal.ai.WritingSuggestionResult
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AIAnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = ClaudeAIService(application)
    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _deepAnalysis = MutableStateFlow<EmotionAnalysisResult?>(null)
    val deepAnalysis: StateFlow<EmotionAnalysisResult?> = _deepAnalysis

    private val _writingSuggestions = MutableStateFlow<WritingSuggestionResult?>(null)
    val writingSuggestions: StateFlow<WritingSuggestionResult?> = _writingSuggestions

    private val _similarEntries = MutableStateFlow<List<SimilarEntry>>(emptyList())
    val similarEntries: StateFlow<List<SimilarEntry>> = _similarEntries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun analyzeEntry(entryId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val entry = repository.getEntryById(entryId)
                if (entry != null) {
                    // 深度情绪分析
                    val analysis = aiService.analyzeEmotionDeep(entry.transcription)
                    _deepAnalysis.value = analysis

                    // 写作建议
                    val suggestions = aiService.getWritingSuggestions(entry.transcription)
                    _writingSuggestions.value = suggestions

                    // 查找相似日记
                    repository.getAllEntries().collect { allEntries ->
                        val historical = allEntries
                            .filter { it.id != entryId }
                            .take(20)
                            .map { formatDate(it.timestamp) to it.transcription }

                        val similar = aiService.findSimilarEntries(
                            entry.transcription,
                            historical
                        )
                        _similarEntries.value = similar
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
