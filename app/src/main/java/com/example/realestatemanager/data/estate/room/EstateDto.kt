package com.example.realestatemanager.data.estate.room

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime

data class EstateDto (

    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val type: String,
    val price: BigDecimal,
    val surface: BigDecimal,
    val rooms: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "amenity_school")
    val amenitySchool: Boolean,
    @ColumnInfo(name = "amenity_park")
    val amenityPark: Boolean,
    @ColumnInfo(name = "amenity_shopping")
    val amenityShopping: Boolean,
    @ColumnInfo(name = "amenity_restaurant")
    val amenityRestaurant: Boolean,
    @ColumnInfo(name = "amenity_transportation")
    val amenityTransportation: Boolean,
    @ColumnInfo(name = "amenity_hospital")
    val amenityHospital: Boolean,
    @ColumnInfo(name = "agent_name")
    val agentName: String,
    @ColumnInfo(name = "entry_date")
    val entryDate: LocalDateTime,
    @ColumnInfo(name = "sale_date")
    val saleDate: LocalDateTime?,
)