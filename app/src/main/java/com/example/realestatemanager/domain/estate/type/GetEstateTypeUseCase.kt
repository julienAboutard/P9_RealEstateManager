package com.example.realestatemanager.domain.estate.type

import com.example.realestatemanager.data.estate.type.EstateType
import com.example.realestatemanager.data.estate.type.EstateTypeRepository
import javax.inject.Inject

class GetEstateTypeUseCase @Inject constructor(
    private val estateTypeRepository: EstateTypeRepository
) {

    fun invoke(): List<EstateType> = estateTypeRepository.getEstateTypes()
}