package com.example.realestatemanager.data.estate

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.estate.entity.EstateEntity
import com.example.realestatemanager.data.estate.room.EstateDto
import com.example.realestatemanager.data.media.MediaMapper
import com.example.realestatemanager.data.media.room.MediaDto
import java.math.RoundingMode
import javax.inject.Inject

class EstateMapper @Inject constructor(
    private val mediaMapper: MediaMapper
){
    fun mapToDto(estate: EstateEntity) = EstateDto(
        id = estate.id,
        type = estate.type,
        price = estate.price,
        surface = estate.surface,
        rooms = estate.rooms,
        bedrooms = estate.bedrooms,
        bathrooms = estate.bathrooms,
        description = estate.description,
        location = estate.location,
        latitude = estate.latitude ?: 0.0,
        longitude = estate.longitude ?: 0.0,
        amenitySchool = estate.amenities.contains(AmenityType.SCHOOL),
        amenityPark = estate.amenities.contains(AmenityType.PARK),
        amenityShopping = estate.amenities.contains(AmenityType.SHOPPING_MALL),
        amenityRestaurant = estate.amenities.contains(AmenityType.RESTAURANT),
        amenityTransportation = estate.amenities.contains(AmenityType.PUBLIC_TRANSPORTATION),
        amenityHospital = estate.amenities.contains(AmenityType.HOSPITAL),
        agentName = estate.agentName,
        entryDate = estate.entryDate,
        saleDate = estate.saleDate,
    )

    fun mapToDomainEntity(
        estateDto: EstateDto,
        listMediaDto: List<MediaDto>,
    ) = EstateEntity(
        id = estateDto.id,
        type = estateDto.type,
        price = estateDto.price.setScale(0, RoundingMode.HALF_UP),
        surface = estateDto.surface.setScale(0, RoundingMode.HALF_UP),
        medias = mediaMapper.mapToDomainEntities(listMediaDto),
        amenities = mapAmenities(estateDto),
        rooms = estateDto.rooms,
        bedrooms = estateDto.bedrooms,
        bathrooms = estateDto.bathrooms,
        location = estateDto.location,
        latitude = estateDto.latitude,
        longitude = estateDto.longitude,
        description = estateDto.description,
        agentName = estateDto.agentName,
        entryDate = estateDto.entryDate,
        saleDate = estateDto.saleDate,
    )

    private fun mapAmenities(estateDto: EstateDto): List<AmenityType> = buildList {
        if (estateDto.amenitySchool) add(AmenityType.SCHOOL)
        if (estateDto.amenityPark) add(AmenityType.PARK)
        if (estateDto.amenityShopping) add(AmenityType.SHOPPING_MALL)
        if (estateDto.amenityRestaurant) add(AmenityType.RESTAURANT)
        if (estateDto.amenityTransportation) add(AmenityType.PUBLIC_TRANSPORTATION)
        if (estateDto.amenityHospital) add(AmenityType.HOSPITAL)
    }
}