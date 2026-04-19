package com.voicejournal.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    fun showShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showSuccess(context: Context, message: String) {
        Toast.makeText(context, "✅ $message", Toast.LENGTH_SHORT).show()
    }

    fun showError(context: Context, message: String) {
        Toast.makeText(context, "❌ $message", Toast.LENGTH_SHORT).show()
    }

    fun showWarning(context: Context, message: String) {
        Toast.makeText(context, "⚠️ $message", Toast.LENGTH_SHORT).show()
    }

    fun showInfo(context: Context, message: String) {
        Toast.makeText(context, "ℹ️ $message", Toast.LENGTH_SHORT).show()
    }
}
