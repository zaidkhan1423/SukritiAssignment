package com.zaid.sukritiassignment.presentation.view_model

import android.media.MediaPlayer
import com.zaid.sukritiassignment.data.model.AudioFile

data class MusicUiState(
    val isMusicPlaying: Boolean = false,
    val playingAudioFile: AudioFile? = null,
    val audioFiles: List<AudioFile> = emptyList(),
    val currentPosition: Int = 0,
    val loading: Boolean = false,
    val snackBarMessage: String? = null,
    val audioFile: AudioFile? = null,
    val playerSeekBarPosition: Int = 0
)