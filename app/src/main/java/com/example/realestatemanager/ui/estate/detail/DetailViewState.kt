package com.example.realestatemanager.ui.estate.detail

import androidx.annotation.StringRes
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.detail.medialist.MediaViewState
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText

data class DetailViewState(
    val id: Long,
    @StringRes val estateType: Int,
    val medias: List<MediaViewState>,
    val mapMiniature: NativePhoto,
    val price: String,
    val surface: String,
    val rooms: NativeText,
    val bathrooms: NativeText,
    val bedrooms: NativeText,
    val description: String,
    val address: NativeText,
    val amenities: List<AmenityViewState.AmenityItem>,
    val entryDate: NativeText,
    val agentName: NativeText,
    val isSold: Boolean,
    val saleDate: NativeText?,
)