package com.example.realestatemanager.domain.autocomplete.address

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import javax.inject.Inject

class UpdateOnAddressClickedUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository,
) {
    suspend fun invoke(isAddressSelected: Boolean) {
        predictionAddressStateRepository.setIsPredictionSelectedByUser(isAddressSelected)
    }
}
