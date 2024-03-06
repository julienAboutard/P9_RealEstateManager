package com.example.realestatemanager.domain.autocomplete.address

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import javax.inject.Inject

class ResetSelectedAddressStateUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository,
) {
    fun invoke() {
        predictionAddressStateRepository.resetSelectedAddressState()
    }
}