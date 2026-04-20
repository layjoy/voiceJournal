package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.settings.SettingsManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    val themeMode: StateFlow<String> = settingsManager.themeMode as StateFlow<String>
    val notificationEnabled: StateFlow<Boolean> = settingsManager.notificationEnabled as StateFlow<Boolean>
    val notificationsEnabled: StateFlow<Boolean> = settingsManager.notificationEnabled as StateFlow<Boolean>
    val dailyReminderEnabled: StateFlow<Boolean> = settingsManager.notificationEnabled as StateFlow<Boolean>
    val autoBackup: StateFlow<Boolean> = settingsManager.autoBackup as StateFlow<Boolean>
    val autoBackupEnabled: StateFlow<Boolean> = settingsManager.autoBackup as StateFlow<Boolean>
    val audioQuality: StateFlow<String> = settingsManager.audioQuality as StateFlow<String>
    val language: StateFlow<String> = settingsManager.language as StateFlow<String>
    val biometricEnabled: StateFlow<Boolean> = settingsManager.biometricEnabled as StateFlow<Boolean>

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setNotificationEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        setNotificationEnabled(enabled)
    }

    fun setDailyReminderEnabled(enabled: Boolean) {
        setNotificationEnabled(enabled)
    }

    fun setAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setAutoBackup(enabled)
        }
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        setAutoBackup(enabled)
    }

    fun setAudioQuality(quality: String) {
        viewModelScope.launch {
            settingsManager.setAudioQuality(quality)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(language)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setBiometricEnabled(enabled)
        }
    }
}
