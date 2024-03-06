package com.example.realestatemanager.domain.filter

import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.filter.EstatesFilterEntity
import com.example.realestatemanager.ui.filter.EstateSaleState
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class IsEstateMatchingFiltersUseCase @Inject constructor() {
    fun invoke(
        estateType: String?,
        address: String?,
        price: BigDecimal,
        surface: BigDecimal,
        media: Int,
        amenities: List<AmenityType>,
        entryDate: LocalDate,
        soldDate: LocalDate?,
        isSold: Boolean,
        estatesFilter: EstatesFilterEntity?
    ): Boolean = estatesFilter == null ||
            doesMatchEstateTypeFilter(estateType, estatesFilter) &&
            doesContainAddressFilter(address, estatesFilter) &&
            doesMatchPriceFilter(price, estatesFilter) &&
            doesMatchSurfaceFilter(surface, estatesFilter) &&
            doesMatchMediaFilter(media, estatesFilter) &&
            doesMatchEntryDateFilter(entryDate, soldDate, estatesFilter) &&
            doesMatchSaleStateFilter(isSold, estatesFilter) &&
            doesMatchAmenitiesFilter(amenities, estatesFilter)

    private fun doesMatchEstateTypeFilter(estateType: String?, filter: EstatesFilterEntity): Boolean =
        filter.estateType == null || estateType == filter.estateType

    private fun doesContainAddressFilter(address: String?, filter: EstatesFilterEntity): Boolean =
        filter.address == null || address == null || address.contains(filter.address, true)

    private fun doesMatchPriceFilter(price: BigDecimal, filter: EstatesFilterEntity): Boolean {
        val (minPrice, maxPrice) = filter.minMaxPrice
        return (minPrice == BigDecimal.ZERO && maxPrice == BigDecimal.ZERO) ||
                (price in minPrice..maxPrice) ||
                (minPrice == BigDecimal.ZERO && price <= maxPrice) ||
                (maxPrice == BigDecimal.ZERO && price >= minPrice)
    }

    private fun doesMatchSurfaceFilter(surface: BigDecimal, filter: EstatesFilterEntity): Boolean {
        val (minSurface, maxSurface) = filter.minMaxSurface
        return (minSurface == BigDecimal.ZERO && maxSurface == BigDecimal.ZERO) ||
                (surface in minSurface..maxSurface) ||
                (minSurface == BigDecimal.ZERO && surface <= maxSurface) ||
                (maxSurface == BigDecimal.ZERO && surface >= minSurface)
    }

    private fun doesMatchMediaFilter(media: Int, filter: EstatesFilterEntity): Boolean =
        filter.minMedia == null || media >= filter.minMedia

    private fun doesMatchEntryDateFilter(entryDate: LocalDate, soldDate: LocalDate?, filter: EstatesFilterEntity): Boolean {
        return if (filter.availableForSale == EstateSaleState.ALL) {
            filter.researchDate == null || entryDate.isAfter(filter.researchDate)
        } else if (filter.availableForSale == EstateSaleState.FOR_SALE) {
            filter.researchDate == null || entryDate.isAfter(filter.researchDate)
        } else {
            soldDate == null || filter.researchDate == null || soldDate.isAfter(filter.researchDate)
        }
    }

    private fun doesMatchSaleStateFilter(isSold: Boolean, filter: EstatesFilterEntity): Boolean =
        filter.availableForSale == EstateSaleState.ALL ||
                filter.availableForSale == EstateSaleState.FOR_SALE && !isSold ||
                filter.availableForSale == EstateSaleState.SOLD && isSold

    private fun doesMatchAmenitiesFilter(amenities: List<AmenityType>, filter: EstatesFilterEntity): Boolean =
        filter.amenities.isEmpty() || amenities.containsAll(filter.amenities)
}