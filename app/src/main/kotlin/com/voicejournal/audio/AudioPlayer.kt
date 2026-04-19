package com.voicejournal.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    fun play(audioPath: String) {
        try {
            stop()

            val file = File(audioPath)
            if (!file.exists()) {
                return
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                start()

                _duration.value = duration
                _isPlaying.value = true

                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = 0
                }
            }

            startPositionUpdater()

        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                startPositionUpdater()
            }
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    private fun startPositionUpdater() {
        Thread {
            while (_isPlaying.value) {
                mediaPlayer?.let {
                    _currentPosition.value = it.currentPosition
                }
                Thread.sleep(100)
            }
        }.start()
    }

    fun release() {
        stop()
    }
}
