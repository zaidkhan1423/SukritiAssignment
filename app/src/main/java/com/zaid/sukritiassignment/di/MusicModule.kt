package com.zaid.sukritiassignment.di

import android.content.Context
import android.media.MediaPlayer
import com.zaid.sukritiassignment.data.repository.MusicRepositoryImpl
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicModule {

    @Provides
    @Singleton
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(@ApplicationContext context: Context,mediaPlayer: MediaPlayer): MusicRepository {
        return MusicRepositoryImpl(context,mediaPlayer)
    }

}