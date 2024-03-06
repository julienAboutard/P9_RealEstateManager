package com.example.realestatemanager.domain.autocomplete.address

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetSelectedAddressStateUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository,
) {
    suspend fun invoke(userInput: String?) {
        if (userInput.isNullOrBlank()) {
            predictionAddressStateRepository.setIsPredictionSelectedByUser(false)
            predictionAddressStateRepository.setCurrentAddressInput(null)
        } else if (predictionAddressStateRepository.getPredictionAddressStateAsFlow()
                .first().isAddressPredictionSelectedByUser == true
        ) {
            predictionAddressStateRepository.setCurrentAddressInput(userInput)
        } else {
            predictionAddressStateRepository.setIsPredictionSelectedByUser(false)
            predictionAddressStateRepository.setCurrentAddressInput(userInput)
        }
    }
}