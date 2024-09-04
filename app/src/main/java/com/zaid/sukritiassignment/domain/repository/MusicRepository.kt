package com.zaid.sukritiassignment.domain.repository

import android.media.MediaPlayer
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun playAudio(audioFile: AudioFile): Resource<Flow<MediaPlayer>>
    fun getAllAudioFiles(): Resource<List<AudioFile>>
}