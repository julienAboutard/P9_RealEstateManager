package com.example.realestatemanager.domain.map_picture

import javax.inject.Inject

class GenerateMapBaseUrlWithParamsUseCase @Inject constructor() {

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"
        private const val CENTER = "center="
        private const val SIZE = "size="
        private const val SIZE_VALUE = "480x320"

        private const val MARKER = "markers="
        private const val MARKER_COLOR = "color:red|"
    }

    fun invoke(lat: Double, lon: Double): String =
        BASE_URL +
                CENTER +
                lat + "," + lon + "&" +
                SIZE + SIZE_VALUE + "&" +
                MARKER + MARKER_COLOR +
                lat + "," + lon +
                "&"
}