package com.voicejournal.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    fun startRecording(): String? {
        try {
            val audioDir = File(context.filesDir, "audio")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }

            outputFile = File(audioDir, "recording_${System.currentTimeMillis()}.m4a")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)

                prepare()
                start()
            }

            _isRecording.value = true
            return outputFile?.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false
            _amplitude.value = 0
            outputFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getMaxAmplitude(): Int {
        return try {
            val amp = mediaRecorder?.maxAmplitude ?: 0
            _amplitude.value = amp
            amp
        } catch (e: Exception) {
            0
        }
    }

    fun release() {
        try {
            mediaRecorder?.release()
            mediaRecorder = null
            _isRecording.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
