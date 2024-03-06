package com.example.realestatemanager.domain.autocomplete

import com.example.realestatemanager.data.autocomplete.PredictionRepository
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import javax.inject.Inject

class GetAddressPredictionsUseCase @Inject constructor(
    private val predictionRepository: PredictionRepository
) {
    suspend fun invoke(query: String): PredictionWrapper = predictionRepository.getAddressPredictions(query)
}
