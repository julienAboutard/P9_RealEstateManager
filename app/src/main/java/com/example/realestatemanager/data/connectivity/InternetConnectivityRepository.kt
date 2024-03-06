package com.example.realestatemanager.data.connectivity

import kotlinx.coroutines.flow.Flow

interface InternetConnectivityRepository {
    fun isInternetEnabledAsFlow(): Flow<Boolean>
}
