package com.example.realestatemanager.domain.amenity

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.amenity.AmenityTypeRepository
import javax.inject.Inject

class GetAmenityTypeUseCase @Inject constructor(
    private val amenityTypeRepository: AmenityTypeRepository,
) {

    fun invoke(): List<AmenityType> = amenityTypeRepository.getAmenityTypes()
}