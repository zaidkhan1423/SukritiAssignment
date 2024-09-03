package com.zaid.sukritiassignment.presentation.view_model

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaid.sukritiassignment.NotificationHelper
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val notificationHelper: NotificationHelper

) : ViewModel() {

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer = _mediaPlayer.asStateFlow()
    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying = _isMusicPlaying.asStateFlow()
    private val _playingAudioFile = MutableStateFlow<AudioFile?>(null)
    val playingAudioFile = _playingAudioFile.asStateFlow()

    private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
    val audioFiles = _audioFiles.asStateFlow()

    init {
        fetchAudioFiles()
    }

    fun playAudio(audioFile: AudioFile) {
        _playingAudioFile.value = audioFile
        viewModelScope.launch(Dispatchers.Main) {
            musicRepository.playAudio(audioFile).collect { player ->
                _mediaPlayer.value = player
                player.start()
                _isMusicPlaying.value = true
            }
            notificationHelper.showNotification(audioFile.name, "Notification Content")
        }
    }


    fun updateSeekBarPosition(): Flow<Int> = flow {
        _mediaPlayer.value?.let { player ->
            while (true) {
                emit(player.currentPosition)
                delay(100L)
            }
        }
    }

    fun seekTo(position: Int) {
        _mediaPlayer.value?.seekTo(position)
    }

    private fun fetchAudioFiles() {
        viewModelScope.launch(Dispatchers.Main) {
            while (audioFiles.value.isEmpty()) {
                _audioFiles.value = musicRepository.getAllAudioFiles()
            }
        }
    }

    fun stopMusic() {
        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.stop()
            }
        }
    }

    fun pauseMusic() {
        _isMusicPlaying.value = false
        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.pause()
            }
        }
    }

    fun startMusic() {
        _isMusicPlaying.value = true
        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.start()
            }
        }
    }

    fun nextTrack() {
        _playingAudioFile.value?.let { currentFile ->
            val currentIndex = _audioFiles.value.indexOf(currentFile)
            if (currentIndex != -1 && currentIndex < _audioFiles.value.size - 1) {
                playAudio(_audioFiles.value[currentIndex + 1])
            }
        }
    }

    fun previousTrack() {
        _playingAudioFile.value?.let { currentFile ->
            val currentIndex = _audioFiles.value.indexOf(currentFile)
            if (currentIndex > 0) {
                playAudio(_audioFiles.value[currentIndex - 1])
            }
        }
    }

}
