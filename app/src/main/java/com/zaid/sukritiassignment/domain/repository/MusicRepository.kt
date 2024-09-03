package com.zaid.sukritiassignment.domain.repository

import android.media.MediaPlayer
import com.zaid.sukritiassignment.data.model.AudioFile
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun playAudio(audioFile: AudioFile): Flow<MediaPlayer>
    fun getAllAudioFiles(): List<AudioFile>
}