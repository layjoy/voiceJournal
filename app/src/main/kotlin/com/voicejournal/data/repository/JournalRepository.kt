package com.voicejournal.data.repository

import com.voicejournal.data.database.JournalDao
import com.voicejournal.data.database.JournalEntryEntity
import com.voicejournal.data.model.Emotion
import com.voicejournal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournalRepository(private val journalDao: JournalDao) {

    fun getAllEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllEntries().map { entities ->
            entities.map { it.toJournalEntry() }
        }
    }

    fun getAllCapsules(): Flow<List<JournalEntry>> {
        return journalDao.getAllCapsules().map { entities ->
            entities.map { it.toJournalEntry() }
        }
    }

    suspend fun getEntryById(id: String): JournalEntry? {
        return journalDao.getEntryById(id)?.toJournalEntry()
    }

    suspend fun insertEntry(entry: JournalEntry) {
        journalDao.insertEntry(entry.toEntity())
    }

    suspend fun updateEntry(entry: JournalEntry) {
        journalDao.updateEntry(entry.toEntity())
    }

    suspend fun deleteEntry(entry: JournalEntry) {
        journalDao.deleteById(entry.id)
    }

    private fun JournalEntryEntity.toJournalEntry(): JournalEntry {
        return JournalEntry(
            id = id,
            audioPath = audioPath,
            transcription = transcription,
            emotion = Emotion.valueOf(emotion),
            timestamp = timestamp,
            duration = duration,
            waveformData = waveformData,
            isCapsule = isCapsule,
            unlockTime = unlockTime
        )
    }

    private fun JournalEntry.toEntity(): JournalEntryEntity {
        return JournalEntryEntity(
            id = id,
            audioPath = audioPath,
            transcription = transcription,
            emotion = emotion.name,
            timestamp = timestamp,
            duration = duration,
            waveformData = waveformData,
            isCapsule = isCapsule,
            unlockTime = unlockTime
        )
    }
}
