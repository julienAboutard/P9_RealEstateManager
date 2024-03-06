package com.example.realestatemanager.data.geolocation

import com.example.realestatemanager.ui.utils.NativeText


sealed class GeolocationState {
    data class Success(val latitude: Double, val longitude: Double) : GeolocationState()
    data class Error(val message: NativeText?) : GeolocationState()
}
