package com.example.realestatemanager.data.amenity

import javax.inject.Inject

class AmenityTypeRepositoryImpl @Inject constructor(): AmenityTypeRepository {
    private val amenityTypes = listOf(
        AmenityType.SCHOOL,
        AmenityType.PARK,
        AmenityType.SHOPPING_MALL,
        AmenityType.RESTAURANT,
        AmenityType.PUBLIC_TRANSPORTATION,
        AmenityType.HOSPITAL
    )

    override fun getAmenityTypes(): List<AmenityType> = amenityTypes
}