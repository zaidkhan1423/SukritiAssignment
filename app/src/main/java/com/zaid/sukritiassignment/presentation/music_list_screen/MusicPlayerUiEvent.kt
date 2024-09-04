package com.zaid.sukritiassignment.presentation.music_list_screen

import com.zaid.sukritiassignment.data.model.AudioFile

sealed interface MusicPlayerUiEvent {

    data object OnStopMusicClick: MusicPlayerUiEvent
    data object OnPauseMusicClick: MusicPlayerUiEvent
    data object OnStartMusicClick: MusicPlayerUiEvent
    data class OnPlayAudioClick(val audioFile: AudioFile) : MusicPlayerUiEvent
    data class OnSeekToClick(val seekPosition: Int) : MusicPlayerUiEvent
    data object OnUpdateSeekBarPosition: MusicPlayerUiEvent
    data object OnPreviousTrackClick: MusicPlayerUiEvent
    data object OnNextTrackClick: MusicPlayerUiEvent
    data object OnFetchAudioFiles: MusicPlayerUiEvent

}