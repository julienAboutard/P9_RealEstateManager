package com.example.realestatemanager.data.geocoding

import com.google.android.gms.maps.model.LatLng

sealed class GeocodingWrapper {
    data class Success(val latLng: LatLng) : GeocodingWrapper()
    object NoResult : GeocodingWrapper()
    data class Error(val error: String) : GeocodingWrapper()
}