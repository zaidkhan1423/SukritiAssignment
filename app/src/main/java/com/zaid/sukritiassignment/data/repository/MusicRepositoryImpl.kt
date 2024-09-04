package com.zaid.sukritiassignment.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import com.zaid.sukritiassignment.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val context: Context,
    private val mediaPlayer: MediaPlayer
) : MusicRepository {

    override fun playAudio(audioFile: AudioFile): Resource<Flow<MediaPlayer>> = try {
        val flow = flow {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, audioFile.uri!!)
            mediaPlayer.prepare()
            emit(mediaPlayer)
        }
        Resource.Success(flow)
    } catch (e: Exception) {
        Resource.Error(e)
    }

    override fun getAllAudioFiles(): Resource<List<AudioFile>> = try {
        val audioList = mutableListOf<AudioFile>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val contentUri: Uri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString()
                )
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtUri = Uri.withAppendedPath(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId.toString()
                )

                audioList.add(AudioFile(id, name, duration, contentUri, albumArtUri))
            }
        }
        Resource.Success(audioList)
    } catch (e: Exception) {
        Resource.Error(e)
    }
}

