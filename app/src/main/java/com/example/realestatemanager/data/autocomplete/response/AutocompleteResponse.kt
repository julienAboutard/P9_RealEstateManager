package com.example.realestatemanager.data.autocomplete.response

import com.example.realestatemanager.data.autocomplete.response.PredictionResponse
import com.google.gson.annotations.SerializedName

data class AutocompleteResponse(
    @SerializedName("predictions") val predictions: List<PredictionResponse>?,
    @SerializedName("status") val status: String?
)
