package com.example.realestatemanager.domain.autocomplete.address

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressState
import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPredictionAddressStateFlowUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository
) {
    fun invoke(): Flow<PredictionAddressState?> = predictionAddressStateRepository.getPredictionAddressStateAsFlow()
}