package com.example.realestatemanager.data.media.entity

import androidx.room.ColumnInfo

data class MediaEntity(
    val uri: String,
    val description: String?,
    val type : String,
    val isFeatured: Boolean
)
