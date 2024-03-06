package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.filter.EstatesFilterEntity
import com.example.realestatemanager.data.filter.EstatesFilterRepository
import com.example.realestatemanager.ui.filter.EstateSaleState
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class SetEstatesFilterUseCase @Inject constructor(
    private val estatesFilterRepository: EstatesFilterRepository
) {
    fun invoke(
        estateType: String?,
        address: String?,
        minPrice: BigDecimal,
        maxPrice: BigDecimal,
        minSurface: BigDecimal,
        maxSurface: BigDecimal,
        minMedia: Int,
        researchDate: LocalDate?,
        saleState: EstateSaleState?,
        selectedAmenities: List<AmenityType>,
    ) = estatesFilterRepository.setEstatesFilter(
        EstatesFilterEntity(
            estateType = if (estateType == "All") null else estateType,
            address = address,
            minMaxPrice = Pair(minPrice, maxPrice),
            minMaxSurface = Pair(minSurface, maxSurface),
            minMedia = minMedia,
            researchDate = researchDate,
            availableForSale = saleState ?: EstateSaleState.ALL,
            amenities = selectedAmenities,
        )
    )
}