package com.example.realestatemanager.domain.form

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.media.entity.MediaEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class FormParams(
    val id: Long = 0L,
    val addOrEditStatus: String = "add",
    val type: String? = null,
    val address: String? = null,
    val isAddressValid: Boolean = false,
    val price: BigDecimal = BigDecimal.ZERO,
    val surface: BigDecimal = BigDecimal.ZERO,
    val description: String? = null,
    val rooms: Int = 0,
    val bathrooms: Int = 0,
    val bedrooms: Int = 0,
    val agent: String? = null,
    val selectedAmenities: List<AmenityType> = emptyList(),
    val mediasInit: List<MediaEntity> = emptyList(),
    val medias: List<MediaEntity> = emptyList(),
    val featuredPictureId: Long? = null,
    val isSold: Boolean = false,
    val entryDate: LocalDate? = null,
    val soldDate: LocalDate? = null,
)