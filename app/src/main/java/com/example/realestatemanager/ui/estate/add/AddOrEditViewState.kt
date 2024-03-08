package com.example.realestatemanager.ui.estate.add

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.realestatemanager.ui.estate.add.address_predictions.PredictionViewState
import com.example.realestatemanager.ui.estate.add.agent.AgentViewStateItem
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.media.MediaPreviewViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.NativeText

data class AddOrEditViewState(
    @StringRes val type: Int?,
    val addressPredictions: List<PredictionViewState>,
    val isAddressValid: Boolean,
    val address: String?,
    val price: String?,
    val surface: String?,
    val description: String?,
    val nbRooms: String?,
    val nbBathrooms: String?,
    val nbBedrooms: String?,
    val amenities: List<AmenityViewState>,
    val medias: List<MediaPreviewViewState>,
    val agents: List<AgentViewStateItem>,
    val selectedAgent: String?,
    val priceCurrencyHint: NativeText,
    @DrawableRes val currencyDrawableRes: Int,
    val surfaceUnit: NativeText,
    val submitButtonText: NativeText,
    val isProgressBarVisible: Boolean,
    val estateTypes: List<TypeViewStateItem>,
    val isSold: Boolean = false,
    val soldDate: String? = null,
    val entryDate: String? =null,
    val areEditItemsVisible: Boolean,
    val isInternetEnabled: Boolean,
)