package com.zaid.sukritiassignment.presentation.view_model

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaid.sukritiassignment.NotificationHelper
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import com.zaid.sukritiassignment.presentation.music_list_screen.MusicPlayerUiEvent
import com.zaid.sukritiassignment.utils.Resource
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
                        player.isLooping = true
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = false, snackBarMessage = null, isMusicPlaying = true
                            )
                        }
                    }
                    notificationHelper.showNotification(audioFile.name, "Playing Music")
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
        viewModelScope.launch(Dispatchers.IO) {
            while (musicUiState.value.audioFiles.isEmpty()) {
                when (val result = musicRepository.getAllAudioFiles()) {
                    is Resource.Error -> {
                        _musicUiState.update { uiState ->
                            uiState.copy(
                                loading = false, snackBarMessage = result.exception.message
                            )
                        }
                        Log.e("MusicVM", "Error")
                    }

                    is Resource.Loading -> {
                        Log.e("MusicVM", "Loading")
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
                                snackBarMessage = "Data Fetch",
                                audioFiles = result.data
                            )
                        }
                        Log.e("MusicVM", "Success")
                    }
                }
            }
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
