package com.example.realestatemanager.data.autocomplete.address

import kotlinx.coroutines.flow.Flow

interface PredictionAddressStateRepository {
    fun setCurrentAddressInput(userInput: String?)
    fun setIsPredictionSelectedByUser(isSelected: Boolean)
    fun setHasAddressFocus(hasFocus: Boolean)
    fun getPredictionAddressStateAsFlow(): Flow<PredictionAddressState>
    fun resetSelectedAddressState()
}
