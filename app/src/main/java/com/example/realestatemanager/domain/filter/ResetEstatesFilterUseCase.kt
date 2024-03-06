package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.filter.EstatesFilterRepository
import javax.inject.Inject

class ResetEstatesFilterUseCase @Inject constructor(
    private val estatesFilterRepository: EstatesFilterRepository
) {
    fun invoke() = estatesFilterRepository.resetEstatesFilter()
}