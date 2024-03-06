package com.example.realestatemanager.domain.autocomplete

import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GetCurrentPredictionAddressesFlowWithDebounceUseCase @Inject constructor(
    private val predictionAddressStateRepository: PredictionAddressStateRepository,
    private val getAddressPredictionsUseCase: GetAddressPredictionsUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun invoke(): Flow<PredictionWrapper?> =
        predictionAddressStateRepository.getPredictionAddressStateAsFlow().mapLatest {
            if (it.currentInput == null ||
                it.currentInput.length < 6 ||
                it.isAddressPredictionSelectedByUser == true ||
                it.hasAddressPredictionFocus == false ||
                it.hasAddressPredictionFocus == null
            ) {
                null
            } else {
                delay(400.milliseconds)
                getAddressPredictionsUseCase.invoke(it.currentInput)
            }
        }.distinctUntilChanged()
}