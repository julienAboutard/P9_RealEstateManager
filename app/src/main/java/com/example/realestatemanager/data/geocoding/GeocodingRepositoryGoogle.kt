package com.example.realestatemanager.data.geocoding

import android.util.LruCache
import com.example.realestatemanager.data.geocoding.response.GeocodingResponse
import com.emplk.realestatemanager.domain.geocoding.GeocodingRepository
import com.example.realestatemanager.data.api.GoogleApi
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import com.example.realestatemanager.di.DataModule
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeocodingRepositoryGoogle @Inject constructor(
    private val googleApi: GoogleApi,
    @DataModule.LruCacheGeocode private val geocodingLruCache: LruCache<String, GeocodingWrapper>,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : GeocodingRepository {

    override suspend fun getLatLong(address: String): GeocodingWrapper = withContext(coroutineDispatcherProvider.io) {
        geocodingLruCache.get(address) ?: try {
            val response = googleApi.getGeocode(address)
            when (response.status) {
                "OK" -> {
                    val result = mapResults(response)

                    if (result != null) {
                        GeocodingWrapper.Success(LatLng(result.lat.toDouble(), result.lng.toDouble()))
                    } else {
                        GeocodingWrapper.Error("Status OK but error while trying to get Geocoding results")
                    }
                }

                "ZERO_RESULTS" -> {
                    GeocodingWrapper.NoResult
                }

                else -> GeocodingWrapper.Error(
                    response.status ?: "Unknown error occurred while trying to get Geocoding results"
                )
            }.also { geocodingLruCache.put(address, it) }

        } catch (e: Exception) {
            coroutineContext.ensureActive()
            GeocodingWrapper.Error(e.message ?: "Unknown error occurred while trying to get Geocoding results")
        }
    }

    private fun mapResults(response: GeocodingResponse?): GeocodingResultEntity? {
        val responseResult = response?.results?.firstOrNull() ?: return null
        val lat = responseResult.geometry?.location?.lat
        val lng = responseResult.geometry?.location?.lng

        if (lat != null && lng != null) {
            return GeocodingResultEntity(lat, lng)
        }
        return null
    }
}