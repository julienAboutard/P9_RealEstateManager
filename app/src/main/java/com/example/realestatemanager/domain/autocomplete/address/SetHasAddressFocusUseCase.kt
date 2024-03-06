package com.example.realestatemanager.domain.autocomplete.address

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import javax.inject.Inject

class SetHasAddressFocusUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository,
) {
    fun invoke(hasFocus: Boolean) {
        predictionAddressStateRepository.setHasAddressFocus(hasFocus)
    }
}