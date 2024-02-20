package com.example.realestatemanager.domain.estate

import com.example.realestatemanager.data.estate.EstateRepository
import javax.inject.Inject

class GetEstatesAsFlowUseCase @Inject constructor(
    private val estateRepository: EstateRepository,
) {
    fun invoke() = estateRepository.getEstatesAsFlow()
}