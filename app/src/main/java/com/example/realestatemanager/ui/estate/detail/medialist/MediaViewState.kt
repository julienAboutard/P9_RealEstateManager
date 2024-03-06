package com.example.realestatemanager.ui.estate.detail.medialist

import com.example.realestatemanager.ui.utils.NativePhoto

data class MediaViewState(
    val mediaUri: NativePhoto,
    val description: String?,
    val type: String,
)