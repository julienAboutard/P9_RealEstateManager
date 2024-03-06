package com.emplk.realestatemanager.domain.geocoding

import com.example.realestatemanager.data.geocoding.GeocodingWrapper

interface GeocodingRepository {
    suspend fun getLatLong(address: String): GeocodingWrapper
}
