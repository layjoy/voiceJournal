package com.voicejournal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey
    val id: String,
    val audioPath: String,
    val transcription: String,
    val emotion: String,
    val timestamp: Long,
    val duration: Int,
    val waveformData: List<Float>,
    val isCapsule: Boolean = false,
    val unlockTime: Long? = null
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        val listType = object : TypeToken<List<Float>>() {}.type
        return gson.fromJson(value, listType)
    }
}
