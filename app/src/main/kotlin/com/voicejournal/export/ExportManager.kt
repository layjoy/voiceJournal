package com.voicejournal.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.voicejournal.data.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportManager(private val context: Context) {

    suspend fun exportToText(entries: List<JournalEntry>): File = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fileName = "voice_journal_${System.currentTimeMillis()}.txt"
        val file = File(context.cacheDir, fileName)

        file.bufferedWriter().use { writer ->
            writer.write("声音日记导出\n")
            writer.write("导出时间: ${dateFormat.format(Date())}\n")
            writer.write("总条目数: ${entries.size}\n")
            writer.write("=" * 50 + "\n\n")

            entries.forEach { entry ->
                writer.write("日期: ${dateFormat.format(Date(entry.timestamp))}\n")
                writer.write("情绪: ${entry.emotion.displayName}\n")
                writer.write("内容:\n${entry.transcription}\n")
                writer.write("-" * 50 + "\n\n")
            }
        }

        file
    }

    suspend fun exportToJson(entries: List<JournalEntry>): File = withContext(Dispatchers.IO) {
        val fileName = "voice_journal_${System.currentTimeMillis()}.json"
        val file = File(context.cacheDir, fileName)

        val jsonArray = entries.map { entry ->
            """
            {
                "id": ${entry.id},
                "timestamp": ${entry.timestamp},
                "transcription": "${entry.transcription.replace("\"", "\\\"")}",
                "emotion": "${entry.emotion.name}",
                "audioPath": "${entry.audioPath}"
            }
            """.trimIndent()
        }

        file.bufferedWriter().use { writer ->
            writer.write("[\n")
            writer.write(jsonArray.joinToString(",\n"))
            writer.write("\n]")
        }

        file
    }

    suspend fun exportToCsv(entries: List<JournalEntry>): File = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fileName = "voice_journal_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        file.bufferedWriter().use { writer ->
            writer.write("ID,时间,情绪,内容\n")
            entries.forEach { entry ->
                val content = entry.transcription.replace("\"", "\"\"").replace("\n", " ")
                writer.write("${entry.id},\"${dateFormat.format(Date(entry.timestamp))}\",${entry.emotion.displayName},\"$content\"\n")
            }
        }

        file
    }

    fun shareFile(file: File, mimeType: String = "text/plain") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "分享日记"))
    }

    suspend fun exportWithAudio(entries: List<JournalEntry>): File = withContext(Dispatchers.IO) {
        val fileName = "voice_journal_full_${System.currentTimeMillis()}.zip"
        val zipFile = File(context.cacheDir, fileName)

        java.util.zip.ZipOutputStream(zipFile.outputStream()).use { zipOut ->
            // 导出文本数据
            val textFile = exportToJson(entries)
            textFile.inputStream().use { input ->
                zipOut.putNextEntry(java.util.zip.ZipEntry("journal_data.json"))
                input.copyTo(zipOut)
                zipOut.closeEntry()
            }

            // 导出音频文件
            entries.forEach { entry ->
                val audioFile = File(entry.audioPath)
                if (audioFile.exists()) {
                    audioFile.inputStream().use { input ->
                        zipOut.putNextEntry(java.util.zip.ZipEntry("audio/${audioFile.name}"))
                        input.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
        }

        zipFile
    }
}

private operator fun String.times(count: Int): String {
    return this.repeat(count)
}
