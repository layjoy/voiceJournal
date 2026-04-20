package com.voicejournal.data.model

data class JournalEntry(
    val id: String,
    val audioPath: String,
    val transcription: String,
    val emotion: Emotion,
    val timestamp: Long,
    val duration: Int,
    val waveformData: List<Float>,
    val isCapsule: Boolean = false,
    val unlockTime: Long? = null,
    val tags: List<String> = emptyList()
)

enum class Emotion(val color: Long, val displayName: String) {
    HAPPY(0xFFFFD700, "开心"),
    SAD(0xFF4169E1, "难过"),
    CALM(0xFF4ECDC4, "平静"),
    EXCITED(0xFFFF6584, "兴奋"),
    ANXIOUS(0xFF9370DB, "焦虑"),
    NEUTRAL(0xFF808080, "中性")
}
