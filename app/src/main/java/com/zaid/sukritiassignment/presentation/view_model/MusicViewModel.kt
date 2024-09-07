package com.zaid.sukritiassignment.presentation.view_model

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaid.sukritiassignment.core.NotificationHelper
import com.zaid.sukritiassignment.core.utils.Resource
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import com.zaid.sukritiassignment.presentation.music_screens.MusicPlayerUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _musicUiState = MutableStateFlow(MusicUiState())
    val musicUiState = _musicUiState.asStateFlow()
    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer = _mediaPlayer.asStateFlow()

    init {
        notificationHelper.hideNotification()
        fetchAudioFiles()
    }

    fun onEvent(event: MusicPlayerUiEvent) {
        when (event) {
            MusicPlayerUiEvent.OnStopMusicClick -> stopMusic()
            is MusicPlayerUiEvent.OnPlayAudioClick -> playAudio(event.audioFile)
            MusicPlayerUiEvent.OnPauseMusicClick -> pauseMusic()
            MusicPlayerUiEvent.OnStartMusicClick -> startMusic()
            MusicPlayerUiEvent.OnUpdateSeekBarPosition -> updateSeekBarPosition()
            MusicPlayerUiEvent.OnNextTrackClick -> nextTrack()
            MusicPlayerUiEvent.OnPreviousTrackClick -> previousTrack()
            is MusicPlayerUiEvent.OnSeekToClick -> seekTo(event.seekPosition)
            MusicPlayerUiEvent.OnFetchAudioFiles -> fetchAudioFiles()
        }
    }

    private fun playAudio(audioFile: AudioFile = musicUiState.value.audioFile!!) {
        _musicUiState.update { uiState ->
            uiState.copy(
                loading = true, snackBarMessage = null, playingAudioFile = audioFile,
            )
        }
        viewModelScope.launch(Dispatchers.Main) {

            when (val result = musicRepository.playAudio(audioFile)) {
                is Resource.Error -> {
                    _musicUiState.update { uiState ->
                        uiState.copy(
                            loading = false, snackBarMessage = result.exception.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _musicUiState.update { uiState ->
                        uiState.copy(
                            loading = true, snackBarMessage = null
                        )
                    }
                }

                is Resource.Success -> {
                    result.data.collect { player ->
                        _mediaPlayer.value = player
                        player.start()
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = false, snackBarMessage = null, isMusicPlaying = true
                            )
                        }
                        player.setOnCompletionListener {
                            nextTrack()
                        }
                        notificationHelper.showNotification(
                            audioFile.name,
                            "Playing Music",
                            musicUiState.value.isMusicPlaying
                        )
                    }
                }
            }
        }
    }

    private fun updateSeekBarPosition() {
        viewModelScope.launch(Dispatchers.Main) {
            _mediaPlayer.value?.let { player ->
                while (true) {
                    _musicUiState.update {
                        it.copy(playerSeekBarPosition = player.currentPosition)
                    }
                    delay(100L)
                }
            }
        }
    }

    private fun seekTo(position: Int) {
        _mediaPlayer.value?.seekTo(position)
    }

    private fun fetchAudioFiles() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            while (musicUiState.value.audioFiles.isEmpty()) {
                when (val result = musicRepository.getAllAudioFiles()) {
                    is Resource.Error -> {
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = false, snackBarMessage = result.exception.message
                            )
                        }
                        Log.e("MusicVM", "fetchAudioFiles Error -> ${result.exception.message}")
                    }

                    is Resource.Loading -> {
                        Log.e("MusicVM", "fetchAudioFiles Loading -> Loading.")
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = true, snackBarMessage = null
                            )
                        }

                    }

                    is Resource.Success -> {
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = false,
                                snackBarMessage = null,
                                audioFiles = result.data
                            )
                        }
                        Log.e("MusicVM", "Success -> ${result.data}")
                    }
                }
            }
        }

        viewModelScope.launch {
            job.start()
            delay(15000)
            job.cancel()
        }

    }

    private fun stopMusic() {

        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.stop()
            }
        }
    }

    private fun startMusic() {
        _musicUiState.update { uiState ->
            uiState.copy(
                isMusicPlaying = true
            )
        }

        notificationHelper.showNotification(
            musicUiState.value.playingAudioFile!!.name,
            "Playing Music",
            musicUiState.value.isMusicPlaying
        )
        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.start()
            }
        }
    }

    private fun pauseMusic() {
        _musicUiState.update { uiState ->
            uiState.copy(
                isMusicPlaying = false
            )
        }
        notificationHelper.showNotification(
            musicUiState.value.playingAudioFile!!.name,
            "Playing Music",
            musicUiState.value.isMusicPlaying
        )
        viewModelScope.launch {
            if (_mediaPlayer.value != null) {
                _mediaPlayer.value!!.pause()
            }
        }
    }

    private fun nextTrack() {
        _musicUiState.value.playingAudioFile?.let { currentFile ->
            val currentIndex = _musicUiState.value.audioFiles.indexOf(currentFile)
            if (currentIndex != -1 && currentIndex < _musicUiState.value.audioFiles.size - 1) {
                playAudio(_musicUiState.value.audioFiles[currentIndex + 1])
            }
        }
    }

    private fun previousTrack() {
        _musicUiState.value.playingAudioFile?.let { currentFile ->
            val currentIndex = _musicUiState.value.audioFiles.indexOf(currentFile)
            if (currentIndex > 0) {
                playAudio(_musicUiState.value.audioFiles[currentIndex - 1])
            }
        }
    }

}
