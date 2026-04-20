package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicejournal.audio.AudioPlayer
import com.voicejournal.audio.AudioRecorder
import com.voicejournal.data.database.JournalDatabase
import com.voicejournal.data.model.Emotion
import com.voicejournal.data.model.JournalEntry
import com.voicejournal.data.repository.JournalRepository
import com.voicejournal.emotion.EmotionAnalyzer
import com.voicejournal.speech.RealtimeSpeechRecognizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val audioRecorder = AudioRecorder(application)
    private val audioPlayer = AudioPlayer(application)
    private val speechRecognizer by lazy { RealtimeSpeechRecognizer(application) }
    private val emotionAnalyzer = EmotionAnalyzer()

    private val repository: JournalRepository

    init {
        val database = JournalDatabase.getDatabase(application)
        repository = JournalRepository(database.journalDao())
    }

    private fun startSpeechRecognitionMonitoring() {
        // 监听实时语音识别结果
        viewModelScope.launch {
            speechRecognizer.recognizedText.collect { text ->
                _transcription.value = text
                if (text.isNotEmpty()) {
                    val emotion = emotionAnalyzer.analyzeEmotion(text)
                    _detectedEmotion.value = emotion
                }
            }
        }
    }

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordingTime = MutableStateFlow(0)
    val recordingTime: StateFlow<Int> = _recordingTime

    private val _currentAudioPath = MutableStateFlow<String?>(null)
    val currentAudioPath: StateFlow<String?> = _currentAudioPath

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription

    private val _detectedEmotion = MutableStateFlow(Emotion.NEUTRAL)
    val detectedEmotion: StateFlow<Emotion> = _detectedEmotion

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun startRecording() {
        val path = audioRecorder.startRecording()
        if (path != null) {
            _isRecording.value = true
            _currentAudioPath.value = path
            _recordingTime.value = 0

            // 启动语音识别监听
            startSpeechRecognitionMonitoring()

            // 重置并启动实时语音识别
            speechRecognizer.reset()
            speechRecognizer.startListening()

            startTimer()
            startAmplitudeMonitor()
        }
    }

    fun stopRecording() {
        val path = audioRecorder.stopRecording()
        _isRecording.value = false

        // 停止实时语音识别
        speechRecognizer.stopListening()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingTime.value += 1
            }
        }
    }

    private fun startAmplitudeMonitor() {
        viewModelScope.launch {
            while (_isRecording.value) {
                val amp = audioRecorder.getMaxAmplitude()
                _amplitude.value = amp
                delay(100)
            }
        }
    }

    fun saveJournalEntry(isCapsule: Boolean = false, unlockTime: Long? = null) {
        viewModelScope.launch {
            _isSaving.value = true

            val entry = JournalEntry(
                id = UUID.randomUUID().toString(),
                audioPath = _currentAudioPath.value ?: "",
                transcription = _transcription.value,
                emotion = _detectedEmotion.value,
                timestamp = System.currentTimeMillis(),
                duration = _recordingTime.value,
                waveformData = emptyList(),
                isCapsule = isCapsule,
                unlockTime = unlockTime
            )

            repository.insertEntry(entry)

            _isSaving.value = false
            resetState()
        }
    }

    private fun resetState() {
        _currentAudioPath.value = null
        _transcription.value = ""
        _detectedEmotion.value = Emotion.NEUTRAL
        _recordingTime.value = 0
        _amplitude.value = 0
        speechRecognizer.reset()
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        audioPlayer.release()
        speechRecognizer.release()
    }
}
