package com.zaid.sukritiassignment.core.di

import android.content.Context
import com.zaid.sukritiassignment.core.NotificationHelper
import com.zaid.sukritiassignment.domain.repository.MusicRepository
import com.zaid.sukritiassignment.presentation.view_model.MusicViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext context: Context): Context {
        return context
    }


    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun providesMusicVM(
        musicRepository: MusicRepository,
        notificationHelper: NotificationHelper
    ): MusicViewModel {
        return MusicViewModel(musicRepository, notificationHelper)
    }

}