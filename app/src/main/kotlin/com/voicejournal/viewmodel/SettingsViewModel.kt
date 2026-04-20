package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.settings.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    val themeMode: StateFlow<String> = settingsManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "system"
    )

    val notificationEnabled: StateFlow<Boolean> = settingsManager.notificationEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val notificationsEnabled: StateFlow<Boolean> = notificationEnabled
    val dailyReminderEnabled: StateFlow<Boolean> = notificationEnabled

    val autoBackup: StateFlow<Boolean> = settingsManager.autoBackup.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val autoBackupEnabled: StateFlow<Boolean> = autoBackup

    val audioQuality: StateFlow<String> = settingsManager.audioQuality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "high"
    )

    val language: StateFlow<String> = settingsManager.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "zh"
    )

    val biometricEnabled: StateFlow<Boolean> = settingsManager.biometricEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

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
