package com.example.realestatemanager.domain.map_picture

import com.example.realestatemanager.BuildConfig
import javax.inject.Inject

class GenerateMapUrlWithApiKeyUseCase @Inject constructor() {
    companion object {
        private const val KEY = "key="
    }

    fun invoke(baseUrlWithParams: String): String = baseUrlWithParams + KEY + BuildConfig.GOOGLE_MAPS_API_KEY
}