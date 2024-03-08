package com.example.realestatemanager.domain.estate

import com.example.realestatemanager.data.estate.EstateRepository
import javax.inject.Inject

class GetEstateByIdUseCase @Inject constructor(
    private val estateRepository: EstateRepository,
) {
    suspend fun invoke(estateId: Long) = estateRepository.getEstateById(estateId)
}