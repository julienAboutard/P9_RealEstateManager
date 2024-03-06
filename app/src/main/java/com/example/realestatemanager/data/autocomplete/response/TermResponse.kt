package com.example.realestatemanager.data.autocomplete.response

import com.google.gson.annotations.SerializedName

data class TermResponse(
    @SerializedName("offset") val offset: Int?,
    @SerializedName("value") val value: String?
)