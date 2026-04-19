package com.voicejournal.backup

import android.content.Context
import com.voicejournal.data.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(private val context: Context) {

    suspend fun createBackup(entries: List<JournalEntry>): File = withContext(Dispatchers.IO) {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        val backupFile = File(backupDir, "backup_${System.currentTimeMillis()}.zip")

        ZipOutputStream(FileOutputStream(backupFile)).use { zipOut ->
            // 备份数据库
            val dbFile = context.getDatabasePath("journal_database")
            if (dbFile.exists()) {
                FileInputStream(dbFile).use { fis ->
                    zipOut.putNextEntry(ZipEntry("database.db"))
                    fis.copyTo(zipOut)
                    zipOut.closeEntry()
                }
            }

            // 备份音频文件
            entries.forEach { entry ->
                val audioFile = File(entry.audioPath)
                if (audioFile.exists()) {
                    FileInputStream(audioFile).use { fis ->
                        zipOut.putNextEntry(ZipEntry("audio/${audioFile.name}"))
                        fis.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
        }

        backupFile
    }

    suspend fun restoreBackup(backupFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val tempDir = File(context.cacheDir, "restore_temp")
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }

            ZipInputStream(FileInputStream(backupFile)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val file = File(tempDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { fos ->
                            zipIn.copyTo(fos)
                        }
                    }
                    entry = zipIn.nextEntry
                }
            }

            // 恢复数据库
            val dbFile = File(tempDir, "database.db")
            if (dbFile.exists()) {
                val targetDb = context.getDatabasePath("journal_database")
                dbFile.copyTo(targetDb, overwrite = true)
            }

            // 恢复音频文件
            val audioDir = File(tempDir, "audio")
            if (audioDir.exists()) {
                val targetAudioDir = File(context.filesDir, "audio")
                audioDir.copyRecursively(targetAudioDir, overwrite = true)
            }

            tempDir.deleteRecursively()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun listBackups(): List<File> {
        val backupDir = File(context.filesDir, "backups")
        return backupDir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    fun deleteBackup(backupFile: File): Boolean {
        return backupFile.delete()
    }
}
