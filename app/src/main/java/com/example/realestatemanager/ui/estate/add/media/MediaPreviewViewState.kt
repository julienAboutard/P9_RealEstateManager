package com.example.realestatemanager.ui.estate.add.media

import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam

data class MediaPreviewViewState(
    val id: Long,
    val type: String,
    val uri: String,
    val isFeatured: Boolean,
    val description: String?,
    val onDeleteEvent: EquatableCallback,
    val onFeaturedEvent: EquatableCallbackWithParam<Boolean>,
    val onDescriptionChanged: EquatableCallbackWithParam<String>,
)
