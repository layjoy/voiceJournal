package com.voicejournal.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CapsuleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val entryId = intent.getStringExtra("entry_id") ?: return
        val notificationManager = CapsuleNotificationManager(context)
        notificationManager.showCapsuleUnlockNotification(entryId)
    }
}
