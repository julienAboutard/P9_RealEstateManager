package com.example.realestatemanager.ui.estate.list

import androidx.annotation.StringRes
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText
import java.time.LocalDate

data class ListEstateViewState(
    val id: Long,
    @StringRes val estateType: Int,
    val featuredPicture: NativePhoto,
    val location: String,
    val price: String,
    val isSold: Boolean,
    val surface: String,
    val room: NativeText,
    val bedroom: NativeText,
    val bathroom: NativeText,
    val entryDate: LocalDate,
    val amenities: List<AmenityType>,
    val onClickEvent: EquatableCallback,
)