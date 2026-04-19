package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.analytics.EmotionAnalytics
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    private val analytics = EmotionAnalytics()

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _emotionTrends = MutableStateFlow<List<EmotionAnalytics.EmotionTrend>>(emptyList())
    val emotionTrends: StateFlow<List<EmotionAnalytics.EmotionTrend>> = _emotionTrends

    private val _timePatterns = MutableStateFlow<List<EmotionAnalytics.TimePattern>>(emptyList())
    val timePatterns: StateFlow<List<EmotionAnalytics.TimePattern>> = _timePatterns

    private val _weeklyReport = MutableStateFlow<EmotionAnalytics.WeeklyReport?>(null)
    val weeklyReport: StateFlow<EmotionAnalytics.WeeklyReport?> = _weeklyReport

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                _emotionTrends.value = analytics.analyzeEmotionTrends(entries, 30)
                _timePatterns.value = analytics.analyzeTimePatterns(entries)
                _weeklyReport.value = analytics.generateWeeklyReport(entries)
            }
        }
    }

    fun refreshAnalytics() {
        loadAnalytics()
    }
}
