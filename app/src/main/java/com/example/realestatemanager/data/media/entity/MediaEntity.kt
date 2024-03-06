package com.example.realestatemanager.data.media.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

data class MediaEntity(
    val id: Long,
    val uri: String,
    var description: String?,
    val type : String,
    var isFeatured: Boolean
)
