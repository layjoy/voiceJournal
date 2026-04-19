package com.voicejournal.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CapsuleAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val capsuleId = intent.getStringExtra("capsule_id") ?: return
        val capsuleTitle = intent.getStringExtra("capsule_title") ?: "未命名胶囊"

        Log.d("CapsuleAlarm", "Capsule unlocked: $capsuleId")

        val notificationManager = CapsuleNotificationManager(context)
        notificationManager.showCapsuleUnlockedNotification(capsuleTitle)
    }
}

class CapsuleAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleCapsuleUnlock(capsuleId: String, capsuleTitle: String, unlockTime: Long) {
        val intent = Intent(context, CapsuleAlarmReceiver::class.java).apply {
            putExtra("capsule_id", capsuleId)
            putExtra("capsule_title", capsuleTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            capsuleId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            unlockTime,
            pendingIntent
        )

        Log.d("CapsuleAlarm", "Scheduled alarm for $capsuleTitle at $unlockTime")
    }

    fun cancelCapsuleUnlock(capsuleId: String) {
        val intent = Intent(context, CapsuleAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            capsuleId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
