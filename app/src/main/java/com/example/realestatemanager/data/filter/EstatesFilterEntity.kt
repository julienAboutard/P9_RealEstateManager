package com.example.realestatemanager.data.filter

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.ui.filter.EstateSaleState
import java.math.BigDecimal
import java.time.LocalDate

data class EstatesFilterEntity(
    val estateType: String? = null,
    val address: String? = null,
    val minMaxPrice: Pair<BigDecimal, BigDecimal> = Pair(BigDecimal.ZERO, BigDecimal.ZERO),
    val minMaxSurface: Pair<BigDecimal, BigDecimal> = Pair(BigDecimal.ZERO, BigDecimal.ZERO),
    val minMedia: Int? = null,
    val researchDate: LocalDate?,
    val availableForSale: EstateSaleState = EstateSaleState.ALL,
    val amenities: List<AmenityType> = emptyList(),
)
