package com.emplk.realestatemanager.domain.filter

import com.example.realestatemanager.fixtures.testFixedClock
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.filter.EstatesFilterEntity
import com.example.realestatemanager.domain.filter.IsEstateMatchingFiltersUseCase
import com.example.realestatemanager.ui.filter.EstateSaleState
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(Parameterized::class)
class IsEstateMatchingFiltersUseCaseTest(
    private val type: String?,
    private val address: String?,
    private val price: BigDecimal,
    private val surface: BigDecimal,
    private val media: Int,
    private val amenities: List<AmenityType>,
    private val entryDate: LocalDate,
    private val soldDate: LocalDate?,
    private val isSold: Boolean,
    private val estatesFilterEntity: EstatesFilterEntity?,
    private val expectedMatch: Boolean,
) {

    private val isEstateMatchingFiltersUseCase = IsEstateMatchingFiltersUseCase()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "{index}: " +
                    "Given estate type [{0}]," +
                    "address [{1}]," +
                    "price [{2}]," +
                    "surface [{3}] sq ft, " +
                    "media [{4}], " +
                    "amenities [{5}], " +
                    "entryDate [{6}]," +
                    "soldDate [{7}]," +
                    "is sold: [{8}], " +
                    "and estateFilter=[{9}]," +
                    "Then it should match: [{10}]"
        )
        fun getValue() = listOf(
            arrayOf(
                null,
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                null,
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                null,
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(estateType = "Villa", researchDate = null),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(estateType = "House", researchDate = null),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(estateType = null, researchDate = null),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(estateType = null, researchDate = null),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(estateType = "Villa", minMaxPrice = Pair(BigDecimal(0), BigDecimal(0)), researchDate = null),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(0), BigDecimal(1000000)),
                    researchDate = null
                ),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1000000),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(2000000),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(3000000),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.now(testFixedClock),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = null
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.of(2023, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.of(2023, 11, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                emptyList<AmenityType>(),
                LocalDate.of(2022, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                emptyList<AmenityType>(),
                LocalDate.of(2021, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL),
                LocalDate.of(2021, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.SCHOOL),
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL),
                LocalDate.of(2021, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.PARK),
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL, AmenityType.PARK),
                LocalDate.of(2021, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.PUBLIC_TRANSPORTATION),
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL, AmenityType.PARK),
                LocalDate.of(2021, 10, 16),
                null,
                false,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.SCHOOL),
                    minMaxPrice = Pair(BigDecimal(1200000), BigDecimal(2000000)),
                    researchDate = LocalDate.of(2023, 9, 1)
                ),
                false
            ),
            // matching is sold
            arrayOf(
                "Villa",
                null,
                BigDecimal(1200000),
                BigDecimal(500),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL, AmenityType.PARK),
                LocalDate.now(testFixedClock),
                null,
                true,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.SCHOOL),
                    availableForSale = EstateSaleState.ALL,
                    researchDate = null
                ),
                true
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                listOf(AmenityType.SCHOOL, AmenityType.PARK),
                LocalDate.now(testFixedClock),
                null,
                true,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.SCHOOL),
                    researchDate = LocalDate.of(2023, 9, 1),
                    availableForSale = EstateSaleState.FOR_SALE,
                ),
                false
            ),
            arrayOf(
                "Villa",
                null,
                BigDecimal(0),
                BigDecimal(0),
                0,
                listOf<AmenityType>(AmenityType.SCHOOL, AmenityType.PARK),
                LocalDate.now(testFixedClock),
                null,
                true,
                EstatesFilterEntity(
                    estateType = "Villa",
                    amenities = listOf(AmenityType.SCHOOL),
                    minMaxPrice = Pair(BigDecimal(1000000), BigDecimal(2000000)),
                    availableForSale = EstateSaleState.SOLD,
                    researchDate = null
                ),
                false
            ),
        )
    }

    @Test
    fun test() {
        assertEquals(
            expectedMatch,
            isEstateMatchingFiltersUseCase.invoke(
                type,
                address,
                price,
                surface,
                media,
                amenities,
                entryDate,
                soldDate,
                isSold,
                estatesFilterEntity
            )
        )
    }
}