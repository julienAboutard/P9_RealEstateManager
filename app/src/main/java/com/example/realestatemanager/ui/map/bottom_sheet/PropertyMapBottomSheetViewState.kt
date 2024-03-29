package com.example.realestatemanager.ui.map.bottom_sheet

import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText

data class PropertyMapBottomSheetViewState(
    val propertyId: Long,
    val type: String,
    val price: String,
    val surface: String,
    val rooms: NativeText,
    val bedrooms: NativeText,
    val bathrooms: NativeText,
    val description: String,
    val featuredPicture: NativePhoto,
    val isSold: Boolean,
    val onDetailClick: EquatableCallbackWithParam<String>,
    val onEditClick: EquatableCallbackWithParam<String>,
    val isProgressBarVisible: Boolean,
)
