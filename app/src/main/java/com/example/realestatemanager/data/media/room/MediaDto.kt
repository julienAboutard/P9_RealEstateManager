package com.example.realestatemanager.data.media.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medias")
data class MediaDto (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    @ColumnInfo(name = "estate_id", index = true)
    val estateId:Long,
    val uri: String,
    val description: String?,
    val type : String,
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean
)