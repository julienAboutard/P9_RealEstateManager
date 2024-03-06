package com.example.realestatemanager.domain.estate.current

import com.example.realestatemanager.data.estate.current.CurrentEstateRepository
import javax.inject.Inject

class ResetCurrentEstateIdUseCase @Inject constructor(
    private val currentEstateRepository: CurrentEstateRepository,
) {
    fun invoke() {
        currentEstateRepository.resetCurrentEstateId()
    }
}