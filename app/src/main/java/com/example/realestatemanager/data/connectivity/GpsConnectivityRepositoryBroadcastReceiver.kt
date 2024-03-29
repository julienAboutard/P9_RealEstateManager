package com.example.realestatemanager.data.connectivity

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsConnectivityRepositoryBroadcastReceiver @Inject constructor(
    private val application: Application,
    private val locationManager: LocationManager?,
) : GpsConnectivityRepository {

    override fun isGpsEnabledAsFlow(): Flow<Boolean> = callbackFlow {
        trySend(hasGpsConnexion())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(hasGpsConnexion())
            }
        }

        application.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        awaitClose {
            application.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()

    private fun hasGpsConnexion(): Boolean =
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
}