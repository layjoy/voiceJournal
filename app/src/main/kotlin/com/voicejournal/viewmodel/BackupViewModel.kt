package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.backup.BackupManager
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class BackupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    private val backupManager = BackupManager(application)

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private val _isBackingUp = MutableStateFlow(false)
    val isBackingUp: StateFlow<Boolean> = _isBackingUp

    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring

    private val _backupFiles = MutableStateFlow<List<File>>(emptyList())
    val backupFiles: StateFlow<List<File>> = _backupFiles

    private val _backupResult = MutableStateFlow<String?>(null)
    val backupResult: StateFlow<String?> = _backupResult

    init {
        loadBackupFiles()
    }

    fun createBackup() {
        viewModelScope.launch {
            _isBackingUp.value = true
            _backupResult.value = null
            try {
                repository.getAllEntries().collect { entries ->
                    val backupFile = backupManager.createBackup(entries)
                    _backupResult.value = "备份成功: ${backupFile.name}"
                    loadBackupFiles()
                }
            } catch (e: Exception) {
                _backupResult.value = "备份失败: ${e.message}"
            } finally {
                _isBackingUp.value = false
            }
        }
    }

    fun restoreBackup(backupFile: File) {
        viewModelScope.launch {
            _isRestoring.value = true
            _backupResult.value = null
            try {
                val success = backupManager.restoreBackup(backupFile)
                _backupResult.value = if (success) "恢复成功" else "恢复失败"
            } catch (e: Exception) {
                _backupResult.value = "恢复失败: ${e.message}"
            } finally {
                _isRestoring.value = false
            }
        }
    }

    fun deleteBackup(backupFile: File) {
        viewModelScope.launch {
            backupManager.deleteBackup(backupFile)
            loadBackupFiles()
        }
    }

    private fun loadBackupFiles() {
        viewModelScope.launch {
            _backupFiles.value = backupManager.listBackups()
        }
    }

    fun clearResult() {
        _backupResult.value = null
    }
}
