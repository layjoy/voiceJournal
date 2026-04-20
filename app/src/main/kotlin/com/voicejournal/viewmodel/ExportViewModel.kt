package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import com.voicejournal.export.ExportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ExportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    private val exportManager = ExportManager(application)

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting

    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress

    private val _exportedFile = MutableStateFlow<File?>(null)
    val exportedFile: StateFlow<File?> = _exportedFile

    fun exportAsText() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                repository.getAllEntries().collect { entries ->
                    val file = exportManager.exportToText(entries)
                    _exportedFile.value = file
                }
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportAsJson() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                repository.getAllEntries().collect { entries ->
                    val file = exportManager.exportToJson(entries)
                    _exportedFile.value = file
                }
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportAsCsv() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                repository.getAllEntries().collect { entries ->
                    val file = exportManager.exportToCsv(entries)
                    _exportedFile.value = file
                }
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun shareFile(file: File, mimeType: String = "text/plain") {
        exportManager.shareFile(file, mimeType)
    }

    fun exportData(format: String) {
        when (format.lowercase()) {
            "text", "txt" -> exportAsText()
            "json" -> exportAsJson()
            "csv" -> exportAsCsv()
            else -> exportAsText()
        }
    }
}
