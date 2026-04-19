package com.voicejournal.constants

object Constants {

    // 音频配置
    const val AUDIO_SAMPLE_RATE = 44100
    const val AUDIO_BIT_RATE = 128000
    const val AUDIO_CHANNELS = 1

    // 录音限制
    const val MAX_RECORDING_DURATION = 600_000L // 10分钟
    const val MIN_RECORDING_DURATION = 1_000L // 1秒

    // 时光胶囊
    const val MIN_CAPSULE_DURATION = 3600_000L // 1小时
    const val MAX_CAPSULE_DURATION = 31536000000L // 1年

    // AI配置
    const val AI_REQUEST_TIMEOUT = 30_000L // 30秒
    const val AI_MAX_TOKENS = 1000

    // 缓存配置
    const val CACHE_SIZE = 50 * 1024 * 1024L // 50MB
    const val CACHE_MAX_AGE = 7 * 24 * 60 * 60 // 7天

    // 分页配置
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // 动画配置
    const val ANIMATION_DURATION = 300
    const val PARTICLE_COUNT = 50
    const val WAVEFORM_LAYERS = 3

    // 通知ID
    const val NOTIFICATION_ID_DAILY = 1
    const val NOTIFICATION_ID_BACKUP = 2
    const val NOTIFICATION_CHANNEL_ID = "voice_journal_channel"

    // 数据库
    const val DATABASE_NAME = "journal_database"
    const val DATABASE_VERSION = 1

    // SharedPreferences
    const val PREFS_NAME = "voice_journal_prefs"

    // 导出格式
    const val EXPORT_FORMAT_TXT = "txt"
    const val EXPORT_FORMAT_JSON = "json"
    const val EXPORT_FORMAT_CSV = "csv"

    // 标签
    const val MAX_TAGS_PER_ENTRY = 10
    const val MAX_TAG_LENGTH = 20

    // 搜索
    const val SEARCH_DEBOUNCE_DELAY = 300L
    const val MIN_SEARCH_LENGTH = 2
}
