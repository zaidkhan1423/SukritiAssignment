package com.zaid.sukritiassignment.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val id: Long,
    val name: String = "",
    val duration: Long,
    val uri: Uri?,
    val albumArtUri: Uri? = null
) : Parcelable