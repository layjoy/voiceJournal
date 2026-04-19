package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.model.JournalEntry
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CapsuleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    val capsules: StateFlow<List<JournalEntry>> = repository.getAllCapsules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isUnlocked(capsule: JournalEntry): Boolean {
        val unlockTime = capsule.unlockTime ?: return true
        return System.currentTimeMillis() >= unlockTime
    }
}
