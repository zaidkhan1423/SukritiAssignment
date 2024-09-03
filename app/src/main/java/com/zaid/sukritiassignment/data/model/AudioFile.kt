package com.zaid.sukritiassignment.data.model

import android.net.Uri

data class AudioFile(
    val id: Long,
    val name: String = "",
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null
)