package com.example.realestatemanager.data.estate.entity

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.media.entity.MediaEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class EstateEntity(
    val id: Long = 0,
    val type: String,
    val price: BigDecimal,
    val surface: BigDecimal,
    val rooms: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val medias: List<MediaEntity>,
    val amenities: List<AmenityType>,
    val agentName: String,
    val entryDate: LocalDateTime,
    val saleDate: LocalDateTime?,
)
