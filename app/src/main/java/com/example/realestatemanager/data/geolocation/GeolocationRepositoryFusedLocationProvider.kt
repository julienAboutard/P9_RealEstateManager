package com.example.realestatemanager.data.geolocation

import androidx.annotation.RequiresPermission
import com.example.realestatemanager.R
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import com.example.realestatemanager.ui.utils.NativeText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class GeolocationRepositoryFusedLocationProvider @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : GeolocationRepository {

    companion object {
        private val LOCATION_INTERVAL_DURATION = 4.seconds
        private const val LOCATION_DISTANCE_THRESHOLD = 50F
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun getCurrentLocationAsFlow(): Flow<GeolocationState> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    trySend(
                        GeolocationState.Success(
                            it.latitude,
                            it.longitude
                        )
                    )
                } ?: trySend(GeolocationState.Error(NativeText.Resource(R.string.geolocation_error_no_location_found)))
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(LOCATION_INTERVAL_DURATION.inWholeMilliseconds)
                .setMinUpdateDistanceMeters(LOCATION_DISTANCE_THRESHOLD)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build(),
            coroutineDispatcherProvider.io.asExecutor(),
            locationCallback
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }.flowOn(coroutineDispatcherProvider.io)
}