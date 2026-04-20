package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TagViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _allTags = MutableStateFlow<List<String>>(emptyList())
    val allTags: StateFlow<List<String>> = _allTags

    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags

    private val _tagUsageCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val tagUsageCount: StateFlow<Map<String, Int>> = _tagUsageCount

    init {
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                val tags = mutableSetOf<String>()
                val usageCount = mutableMapOf<String, Int>()
                entries.forEach { entry ->
                    val tagPattern = "#(\\w+)".toRegex()
                    tagPattern.findAll(entry.transcription).forEach { match ->
                        val tag = match.groupValues[1]
                        tags.add(tag)
                        usageCount[tag] = (usageCount[tag] ?: 0) + 1
                    }
                }
                _allTags.value = tags.sorted()
                _tagUsageCount.value = usageCount
            }
        }
    }

    fun toggleTag(tag: String) {
        val current = _selectedTags.value.toMutableList()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _selectedTags.value = current
    }

    fun clearTags() {
        _selectedTags.value = emptyList()
    }

    fun loadAllTags() {
        loadTags()
    }

    fun addTag(tag: String) {
        viewModelScope.launch {
            val current = _allTags.value.toMutableList()
            if (!current.contains(tag)) {
                current.add(tag)
                _allTags.value = current.sorted()
            }
        }
    }

    fun deleteTag(tag: String) {
        viewModelScope.launch {
            val current = _allTags.value.toMutableList()
            current.remove(tag)
            _allTags.value = current
        }
    }
}
