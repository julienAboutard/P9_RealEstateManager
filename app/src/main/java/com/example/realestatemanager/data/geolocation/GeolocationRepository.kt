package com.example.realestatemanager.data.geolocation

import kotlinx.coroutines.flow.Flow

interface GeolocationRepository {
    fun getCurrentLocationAsFlow(): Flow<GeolocationState>
}
