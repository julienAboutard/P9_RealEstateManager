package com.example.realestatemanager.domain.estate.current

import com.example.realestatemanager.data.estate.current.CurrentEstateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentEstateIdFlowUseCase @Inject constructor(
    private val currentEstateRepository: CurrentEstateRepository,
) {
    fun invoke(): Flow<Long?> = currentEstateRepository.getCurrentEstateIdAsFlow()
}