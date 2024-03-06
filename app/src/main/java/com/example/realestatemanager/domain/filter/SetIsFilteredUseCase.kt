package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.filter.EstatesFilterRepository
import javax.inject.Inject

class SetIsFilteredUseCase @Inject constructor(
    private val estatesFilterRepository: EstatesFilterRepository
) {

    fun invoke(isFiltered: Boolean) = estatesFilterRepository.setIsFiltered(isFiltered)
}