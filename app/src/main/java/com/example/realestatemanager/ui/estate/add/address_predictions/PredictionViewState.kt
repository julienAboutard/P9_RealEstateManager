package com.example.realestatemanager.ui.estate.add.address_predictions

import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam

data class PredictionViewState(
    val address: String,
    val onClickEvent: EquatableCallbackWithParam<String>,
)
