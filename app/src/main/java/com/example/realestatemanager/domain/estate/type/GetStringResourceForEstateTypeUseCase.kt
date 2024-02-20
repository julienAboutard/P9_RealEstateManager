package com.example.realestatemanager.domain.estate.type

import com.example.realestatemanager.data.estate.type.EstateTypeRepository
import javax.inject.Inject

class GetStringResourceForEstateTypeUseCase @Inject constructor(
    private val estateTypeRepository: EstateTypeRepository,
) {

    fun invoke(type: String): Int {
        return estateTypeRepository.getEstateTypes()
            .first { it.databaseName == type }
            .stringRes
    }
}