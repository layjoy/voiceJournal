package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.model.Emotion
import com.voicejournal.data.model.JournalEntry
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class EmotionCalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = JournalRepository(JournalDatabase.getDatabase(application).journalDao())

    private val _calendarData = MutableStateFlow<Map<Int, Emotion?>>(emptyMap())
    val calendarData: StateFlow<Map<Int, Emotion?>> = _calendarData.asStateFlow()

    private val _currentMonth = MutableStateFlow(System.currentTimeMillis())
    val currentMonth: StateFlow<Long> = _currentMonth.asStateFlow()

    private val _emotionStats = MutableStateFlow<Map<Emotion, Int>>(emptyMap())
    val emotionStats: StateFlow<Map<Emotion, Int>> = _emotionStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCalendarData()
    }

    fun loadCalendarData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllEntries().collect { entries ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = _currentMonth.value

                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)

                    // 按日期分组
                    val entriesByDay = entries
                        .filter { entry ->
                            val entryCalendar = Calendar.getInstance()
                            entryCalendar.timeInMillis = entry.timestamp
                            entryCalendar.get(Calendar.YEAR) == year &&
                            entryCalendar.get(Calendar.MONTH) == month
                        }
                        .groupBy { entry ->
                            val entryCalendar = Calendar.getInstance()
                            entryCalendar.timeInMillis = entry.timestamp
                            entryCalendar.get(Calendar.DAY_OF_MONTH)
                        }

                    // 计算每天的主要情绪
                    val dayEmotions = entriesByDay.mapValues { (_, dayEntries) ->
                        dayEntries
                            .groupBy { it.emotion }
                            .maxByOrNull { it.value.size }
                            ?.key
                    }

                    _calendarData.value = dayEmotions

                    // 计算本月情绪统计
                    val stats = entries
                        .filter { entry ->
                            val entryCalendar = Calendar.getInstance()
                            entryCalendar.timeInMillis = entry.timestamp
                            entryCalendar.get(Calendar.YEAR) == year &&
                            entryCalendar.get(Calendar.MONTH) == month
                        }
                        .groupBy { it.emotion }
                        .mapValues { it.value.size }

                    _emotionStats.value = stats
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousMonth() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _currentMonth.value
        calendar.add(Calendar.MONTH, -1)
        _currentMonth.value = calendar.timeInMillis
        loadCalendarData()
    }

    fun nextMonth() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _currentMonth.value
        calendar.add(Calendar.MONTH, 1)
        _currentMonth.value = calendar.timeInMillis
        loadCalendarData()
    }

    fun getEntriesForDay(day: Int, callback: (List<JournalEntry>) -> Unit) {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = _currentMonth.value

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)

                val dayEntries = entries.filter { entry ->
                    val entryCalendar = Calendar.getInstance()
                    entryCalendar.timeInMillis = entry.timestamp
                    entryCalendar.get(Calendar.YEAR) == year &&
                    entryCalendar.get(Calendar.MONTH) == month &&
                    entryCalendar.get(Calendar.DAY_OF_MONTH) == day
                }

                callback(dayEntries)
            }
        }
    }
}
