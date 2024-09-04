package com.zaid.sukritiassignment.core.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed class Screen : Parcelable {

    @Parcelize
    @Serializable
    data object MusicListScreen: Screen()

    @Parcelize
    @Serializable
    data object MusicPlayerScreen: Screen()

}