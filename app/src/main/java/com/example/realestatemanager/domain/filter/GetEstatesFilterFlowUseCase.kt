package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.filter.EstatesFilterEntity
import com.example.realestatemanager.data.filter.EstatesFilterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEstatesFilterFlowUseCase @Inject constructor(
    private val estatesFilterRepository: EstatesFilterRepository
) {
    fun invoke(): Flow<EstatesFilterEntity?> = estatesFilterRepository.getEstatesFilter()
}