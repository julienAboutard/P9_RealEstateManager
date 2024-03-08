package com.example.realestatemanager.fixtures

import com.example.realestatemanager.data.agent.RealEstateAgent
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.estate.EstateWithDetails
import com.example.realestatemanager.data.estate.entity.EstateEntity
import com.example.realestatemanager.data.estate.room.EstateDto
import com.example.realestatemanager.data.estate.type.EstateType
import com.example.realestatemanager.data.media.entity.MediaEntity
import com.example.realestatemanager.data.media.room.MediaDto
import com.example.realestatemanager.domain.form.FormParams
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * A fixed clock that always returns 31 Oct 2023 13:22:35 GMT
 */
val testFixedClock: Clock =
    Clock.fixed(Instant.ofEpochMilli(1698758555000L), ZoneOffset.UTC)

// region PropertyDto

fun getTestEstateDto(id: Long) = EstateDto(
    id = id,
    type = "House",
    price = BigDecimal(1000000),
    surface = BigDecimal(500),
    rooms = 5,
    bedrooms = 3,
    bathrooms = 2,
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    amenitySchool = true,
    amenityPark = true,
    amenityShopping = true,
    amenityRestaurant = false,
    amenityTransportation = false,
    amenityHospital = false,
    agentName = "Shiro Almira",
    saleDate = null,
    entryDate = LocalDate.of(2023, 1, 1),
    latitude = 123.0,
    longitude = 456.0,
    location = "1st, Dummy Street, 12345, Dummy City",
)


// region PropertyEntity

fun getTestEstateEntity(id: Long) = EstateEntity(
    id = id,
    type = "House",
    price = BigDecimal(1000000),
    surface = BigDecimal(500),
    location = "1st, Dummy Street, 12345, Dummy City",
    rooms = 5,
    bedrooms = 3,
    bathrooms = 2,
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    agentName = "Shiro Almira",
    medias = buildList {
        add(
            MediaEntity(
                id = 1L,
                uri = "https://www.google.com/front_view",
                description = "Front view",
                type = "pic",
                isFeatured = true,
            )
        )
        add(
            MediaEntity(
                id = 2L,
                uri = "https://www.google.com/garden",
                description = "Garden",
                type = "vid",
                isFeatured = false,
            )
        )
        add(
            MediaEntity(
                id = 3L,
                uri = "https://www.google.com/swimming_pool",
                description = "Swimming pool",
                type = "pic",
                isFeatured = false,
            )
        )
    },
    amenities = buildList {
        add(AmenityType.SCHOOL)
        add(AmenityType.PARK)
        add(AmenityType.SHOPPING_MALL)
    },
    saleDate = null,
    entryDate = LocalDate.of(2023, 1, 1),
    latitude = 123.0,
    longitude = 456.0,
)

fun getEstateEntities(n: Long) = buildList {
    for (i in 1..n) {
        add(getTestEstateEntity(i))
    }
}
// endregion PropertyEntity

// region PropertyWithDetail
fun getEstateWithDetail(propertyId: Long) = EstateWithDetails(
    estate = getTestEstateDto(propertyId),
    medias = getTestMediaDto(propertyId),
)
// endregion PropertyWithDetail


// region Picture
fun getTestMediaDto(estateId: Long) = buildList {
    add(
        MediaDto(
            estateId = estateId,
            uri = "https://www.google.com/front_view",
            description = "Front view",
            type = "pic",
            isFeatured = true,
        )
    )
    add(
        MediaDto(
            estateId = estateId,
            uri = "https://www.google.com/garden",
            description = "Garden",
            type = "vid",
            isFeatured = false,
        )
    )
    add(
        MediaDto(
            estateId = estateId,
            uri = "https://www.google.com/swimming_pool",
            description = "Swimming pool",
            type = "pic",
            isFeatured = false,
        )
    )
}

fun getTestMediaEntities() = listOf(
    MediaEntity(
        id = 1L,
        uri = "https://www.google.com/front_view",
        description = "Front view",
        type = "pic",
        isFeatured = true,
    ),
    MediaEntity(
        id = 2L,
        uri = "https://www.google.com/garden",
        description = "Garden",
        type = "vid",
        isFeatured = false,
    ),
    MediaEntity(
        id = 3L,
        uri = "https://www.google.com/swimming_pool",
        description = "Swimming pool",
        type = "pic",
        isFeatured = false,
    ),
)
// endregion Picture


// region mappers
fun mapEstateEntityToDto(estate: EstateEntity) = EstateDto(
    id = estate.id,
    type = estate.type,
    price = estate.price,
    surface = estate.surface,
    rooms = estate.rooms,
    bedrooms = estate.bedrooms,
    bathrooms = estate.bathrooms,
    description = estate.description,
    amenitySchool = estate.amenities.contains(AmenityType.SCHOOL),
    amenityPark = estate.amenities.contains(AmenityType.PARK),
    amenityShopping = estate.amenities.contains(AmenityType.SHOPPING_MALL),
    amenityRestaurant = estate.amenities.contains(AmenityType.RESTAURANT),
    amenityTransportation = estate.amenities.contains(AmenityType.PUBLIC_TRANSPORTATION),
    amenityHospital = estate.amenities.contains(AmenityType.HOSPITAL),
    agentName = estate.agentName,
    entryDate = estate.entryDate,
    saleDate = estate.saleDate,
    location = estate.location,
    longitude = estate.longitude,
    latitude = estate.latitude,
)

// endregion mappers

// region FormDraftParams

fun getTestAddFormParams() = FormParams(
    addOrEditStatus = "add",
    type = "House",
    address = "1st, Dummy Street, 12345, Dummy City",
    isAddressValid = true,
    price = BigDecimal(1000000),
    surface = BigDecimal(500),
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    rooms = 5,
    bathrooms = 2,
    bedrooms = 3,
    agent = "Shiro Almira",
    selectedAmenities = buildList {
        add(AmenityType.SCHOOL)
        add(AmenityType.PARK)
        add(AmenityType.SHOPPING_MALL)
    },
    medias = buildList {
        MediaEntity(
            id = 1L,
            uri = "https://www.google.com/front_view",
            description = "Front view",
            type = "pic",
            isFeatured = true,
        )
        MediaEntity(
            id = 2L,
            uri = "https://www.google.com/garden",
            description = "Garden",
            type = "vid",
            isFeatured = false,
        )
        MediaEntity(
            id = 3L,
            uri = "https://www.google.com/swimming_pool",
            description = "Swimming pool",
            type = "pic",
            isFeatured = false,
        )
    },
    isSold = false,
    soldDate = null,
)

fun getTestEditFormParams(id: Long) = FormParams(
    id = id,
    addOrEditStatus = "edit",
    type = "House",
    address = "1st, Dummy Street, 12345, Dummy City",
    isAddressValid = true,
    price = BigDecimal(1000000),
    surface = BigDecimal(500),
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    rooms = 5,
    bathrooms = 2,
    bedrooms = 3,
    agent = "Shiro Almira",
    selectedAmenities = buildList {
        add(AmenityType.SCHOOL)
        add(AmenityType.PARK)
        add(AmenityType.SHOPPING_MALL)
    },
    medias = buildList {
        MediaEntity(
            id = 1L,
            uri = "https://www.google.com/front_view",
            description = "Front view",
            type = "pic",
            isFeatured = true,
        )
        MediaEntity(
            id = 2L,
            uri = "https://www.google.com/garden",
            description = "Garden",
            type = "vid",
            isFeatured = false,
        )
        MediaEntity(
            id = 3L,
            uri = "https://www.google.com/swimming_pool",
            description = "Swimming pool",
            type = "pic",
            isFeatured = false,
        )
    },
    isSold = false,
    entryDate = LocalDate.of(2023, 1, 1),
    soldDate = null,
)
// endregion FormDraftParams


fun getTestAgents() = listOf(
    RealEstateAgent.SHIRO_ALMIRA,
    RealEstateAgent.KURO_ALMIRA,
    RealEstateAgent.NAECHRA_ALMIRA,
    RealEstateAgent.LENARI_ALMIRA,
    RealEstateAgent.SENERA_ALMIRA,
    RealEstateAgent.TO_DEFINE,
)

fun getTestEstateTypes() = listOf(
    EstateType.HOUSE,
    EstateType.FLAT,
    EstateType.DUPLEX,
    EstateType.PENTHOUSE,
    EstateType.VILLA,
    EstateType.MANOR,
    EstateType.OTHER,
)

fun getTestEstateTypesForFilter() = listOf(
    EstateType.ALL,
    EstateType.HOUSE,
    EstateType.FLAT,
    EstateType.DUPLEX,
    EstateType.PENTHOUSE,
    EstateType.VILLA,
    EstateType.MANOR,
    EstateType.OTHER,
)

fun getTestAmenities() = listOf(
    AmenityType.SCHOOL,
    AmenityType.PARK,
    AmenityType.SHOPPING_MALL,
    AmenityType.RESTAURANT,
    AmenityType.PUBLIC_TRANSPORTATION,
    AmenityType.HOSPITAL,
)

