package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.filter.EstatesFilterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsFilteredUseCase @Inject constructor(
    private val estatesFilterRepository: EstatesFilterRepository
) {
    fun invoke(): Flow<Boolean?> = estatesFilterRepository.getIsFiltered()
}