package com.example.realestatemanager.domain.geolocation

import com.example.realestatemanager.R
import com.example.realestatemanager.data.geolocation.GeolocationState
import com.example.realestatemanager.data.connectivity.GpsConnectivityRepository
import com.example.realestatemanager.data.geolocation.GeolocationRepository
import com.example.realestatemanager.domain.permission.HasLocationPermissionFlowUseCase
import com.example.realestatemanager.ui.utils.NativeText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val hasLocationPermissionFlowUseCase: HasLocationPermissionFlowUseCase,
    private val gpsConnectivityRepository: GpsConnectivityRepository,
    private val geolocationRepository: GeolocationRepository,
) {
    @ExperimentalCoroutinesApi
    fun invoke(): Flow<GeolocationState> =
        combine(
            hasLocationPermissionFlowUseCase.invoke(),
            gpsConnectivityRepository.isGpsEnabledAsFlow(),
        ) { isPermissionGranted, isGpsEnabled ->
            if (isPermissionGranted != true) {
                FetchLocationState.NO_PERMISSION
            } else if (!isGpsEnabled) {
                FetchLocationState.GPS_DISABLED
            } else {
                FetchLocationState.CAN_FETCH_LOCATION
            }
        }.distinctUntilChanged()
            .flatMapLatest { state ->
                when (state) {
                    FetchLocationState.CAN_FETCH_LOCATION -> geolocationRepository.getCurrentLocationAsFlow()
                    FetchLocationState.GPS_DISABLED -> flowOf(GeolocationState.Error(NativeText.Resource(R.string.geolocation_error_no_gps)))
                    FetchLocationState.NO_PERMISSION -> flowOf(GeolocationState.Error(null))
                }
            }
}

private enum class FetchLocationState {
    CAN_FETCH_LOCATION,
    GPS_DISABLED,
    NO_PERMISSION,
}