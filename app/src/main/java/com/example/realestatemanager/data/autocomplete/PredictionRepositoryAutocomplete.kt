package com.emplk.realestatemanager.data.autocomplete

import android.util.LruCache
import com.example.realestatemanager.data.autocomplete.response.AutocompleteResponse
import com.example.realestatemanager.data.api.GoogleApi
import com.example.realestatemanager.data.autocomplete.PredictionRepository
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import com.example.realestatemanager.di.DataModule
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PredictionRepositoryAutocomplete @Inject constructor(
    private val googleApi: GoogleApi,
    @DataModule.LruCachePredictions private val predictionsLruCache: LruCache<String, PredictionWrapper>,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : PredictionRepository {

    private companion object {
        private const val TYPE = "address"
    }

    override suspend fun getAddressPredictions(query: String): PredictionWrapper =
        withContext(coroutineDispatcherProvider.io) {
            predictionsLruCache.get(query) ?: try {
                val response = googleApi.getAddressPredictions(query, TYPE)
                when (response.status) {
                    "OK" -> {
                        val predictions = mapResponseToPredictionWrapper(response)
                        if (predictions.isEmpty()) PredictionWrapper.NoResult
                        else PredictionWrapper.Success(predictions)
                    }

                    "ZERO_RESULTS" -> PredictionWrapper.NoResult
                    else -> PredictionWrapper.Failure(Throwable(response.status))
                }.also { predictionsLruCache.put(query, it) }
            } catch (e: Exception) {
                e.printStackTrace()
                PredictionWrapper.Error(e.message ?: "Unknown error occurred")
            }
        }

    private fun mapResponseToPredictionWrapper(response: AutocompleteResponse): List<String> =
        response.predictions?.mapNotNull { prediction ->
            if (prediction.description.isNullOrBlank()) return@mapNotNull null
            else prediction.description
        } ?: emptyList()
}