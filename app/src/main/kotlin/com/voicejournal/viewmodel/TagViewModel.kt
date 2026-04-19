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

    init {
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                val tags = mutableSetOf<String>()
                entries.forEach { entry ->
                    val tagPattern = "#(\\w+)".toRegex()
                    tagPattern.findAll(entry.transcription).forEach { match ->
                        tags.add(match.groupValues[1])
                    }
                }
                _allTags.value = tags.sorted()
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
}
