package com.example.realestatemanager.data.estate

import androidx.room.Embedded
import androidx.room.Relation
import com.example.realestatemanager.data.estate.room.EstateDto
import com.example.realestatemanager.data.media.room.MediaDto

data class EstateWithDetails (
    @Embedded
    val estate: EstateDto,
    @Relation(
        parentColumn = "id",
        entityColumn = "estate_id"
    ) val medias: List<MediaDto>
)