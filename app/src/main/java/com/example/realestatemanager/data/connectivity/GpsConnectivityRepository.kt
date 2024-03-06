package com.example.realestatemanager.data.connectivity

import kotlinx.coroutines.flow.Flow

interface GpsConnectivityRepository {
    fun isGpsEnabledAsFlow(): Flow<Boolean>
}
