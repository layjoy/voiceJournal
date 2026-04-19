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
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<JournalEntry>>(emptyList())
    val searchResults: StateFlow<List<JournalEntry>> = _searchResults

    private val _selectedEmotion = MutableStateFlow<Emotion?>(null)
    val selectedEmotion: StateFlow<Emotion?> = _selectedEmotion

    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        search()
    }

    fun selectEmotion(emotion: Emotion?) {
        _selectedEmotion.value = emotion
        search()
    }

    fun toggleTag(tag: String) {
        val current = _selectedTags.value.toMutableList()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _selectedTags.value = current
        search()
    }

    fun search() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                var filtered = entries

                // 按文本搜索
                if (_searchQuery.value.isNotBlank()) {
                    filtered = filtered.filter {
                        it.transcription.contains(_searchQuery.value, ignoreCase = true)
                    }
                }

                // 按情绪筛选
                _selectedEmotion.value?.let { emotion ->
                    filtered = filtered.filter { it.emotion == emotion }
                }

                // 按标签筛选
                if (_selectedTags.value.isNotEmpty()) {
                    filtered = filtered.filter { entry ->
                        _selectedTags.value.any { tag ->
                            entry.tags.contains(tag)
                        }
                    }
                }

                _searchResults.value = filtered
            }
        }
    }
}
