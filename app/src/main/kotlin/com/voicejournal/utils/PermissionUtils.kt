package com.voicejournal.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { hasPermission(context, it) }
    }

    fun hasRecordAudioPermission(context: Context): Boolean {
        return hasPermission(context, android.Manifest.permission.RECORD_AUDIO)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }
}
