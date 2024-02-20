package com.example.realestatemanager.data.amenity

interface AmenityTypeRepository {
    fun getAmenityTypes(): List<AmenityType>
}