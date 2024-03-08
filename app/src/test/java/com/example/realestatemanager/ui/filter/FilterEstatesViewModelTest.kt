package com.example.realestatemanager.ui.filter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestatemanager.fixtures.getTestEstateTypesForFilter
import com.example.realestatemanager.R
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.amenity.GetAmenityTypeUseCase
import com.example.realestatemanager.domain.estate.type.GetEstateTypeUseCase
import com.example.realestatemanager.domain.filter.SetEstatesFilterUseCase
import com.example.realestatemanager.domain.filter.SetIsFilteredUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareFeetDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToUsdDependingOnLocaleUseCase
import com.example.realestatemanager.domain.navigation.GetNavigationTypeUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.NativeText
import com.example.utils.TestCoroutineRule
import com.example.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class FilterEstatesViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase = mockk()
    private val convertToUsdDependingOnLocaleUseCase: ConvertToUsdDependingOnLocaleUseCase = mockk()
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase = mockk()
    private val convertToSquareFeetDependingOnLocaleUseCase: ConvertToSquareFeetDependingOnLocaleUseCase = mockk()
    private val getEstateTypeUseCase: GetEstateTypeUseCase = mockk()
    private val getAmenityTypeUseCase: GetAmenityTypeUseCase = mockk()
    private val setEstatesFilterUseCase: SetEstatesFilterUseCase = mockk()
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase = mockk()
    private val getNavigationTypeUseCase: GetNavigationTypeUseCase = mockk()
    private val setIsFilteredUseCase: SetIsFilteredUseCase = mockk()

    private lateinit var viewModel: FilterPropertiesViewModel

    @Before
    fun setUp() {
        every { convertToSquareFeetDependingOnLocaleUseCase.invoke(BigDecimal(100)) } returns BigDecimal(100)
        every { convertToSquareFeetDependingOnLocaleUseCase.invoke(BigDecimal(200)) } returns BigDecimal(200)
        every { convertToSquareFeetDependingOnLocaleUseCase.invoke(BigDecimal.ZERO) } returns BigDecimal.ZERO
        coEvery { convertToUsdDependingOnLocaleUseCase.invoke(BigDecimal(100000)) } returns BigDecimal(100000)
        coEvery { convertToUsdDependingOnLocaleUseCase.invoke(BigDecimal(200000)) } returns BigDecimal(200000)
        coEvery { convertToUsdDependingOnLocaleUseCase.invoke(BigDecimal.ZERO) } returns BigDecimal.ZERO
        every { convertToSquareMeterDependingOnLocaleUseCase.invoke(BigDecimal(100)) } returns BigDecimal(100)
        every { convertToSquareMeterDependingOnLocaleUseCase.invoke(BigDecimal(200)) } returns BigDecimal(200)
        every { convertToSquareMeterDependingOnLocaleUseCase.invoke(BigDecimal.ZERO) } returns BigDecimal.ZERO
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal(100000)) } returns BigDecimal(100000)
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal(200000)) } returns BigDecimal(200000)
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal.ZERO) } returns BigDecimal.ZERO
        every { getEstateTypeUseCase.invoke() } returns getTestEstateTypesForFilter()
        every { getAmenityTypeUseCase.invoke() } returns amenityTypes
        every { getNavigationTypeUseCase.invoke() } returns flowOf(NavigationFragmentType.SLIDING_FRAGMENT)
        justRun { setEstatesFilterUseCase.invoke(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        justRun { setNavigationTypeUseCase.invoke(any()) }
        justRun { setIsFilteredUseCase.invoke(any()) }


        viewModel = FilterPropertiesViewModel(
            convertToEuroDependingOnLocaleUseCase,
            convertToUsdDependingOnLocaleUseCase,
            convertToSquareMeterDependingOnLocaleUseCase,
            convertToSquareFeetDependingOnLocaleUseCase,
            getEstateTypeUseCase,
            getAmenityTypeUseCase,
            setEstatesFilterUseCase,
            setNavigationTypeUseCase,
            getNavigationTypeUseCase,
            setIsFilteredUseCase,
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // Given
        viewModel.onPropertyTypeSelected("House")
        viewModel.onMinPriceChanged("100000")
        viewModel.onMaxPriceChanged("200000")
        viewModel.onMinSurfaceChanged("100")
        viewModel.onMaxSurfaceChanged("200")
        viewModel.onEntryDateRangeStatusChanged(LocalDate.now())
        viewModel.onPropertySaleStateChanged(EstateSaleState.ALL)
        viewModel.onAddressChanged("")
        viewModel.onMediaMinChanged("")

        // When
        viewModel.viewState.observeForTesting(this) {

            // Then
            assertEquals(testFilterViewState, it.value)
        }
    }

    @Test
    fun `on reset filter - should reset viewState`() = testCoroutineRule.runTest {
        // Given
        viewModel.onPropertyTypeSelected("House")
        viewModel.onMinPriceChanged("100000")
        viewModel.onMaxPriceChanged("200000")
        viewModel.onMinSurfaceChanged("100")
        viewModel.onMaxSurfaceChanged("200")
        viewModel.onEntryDateRangeStatusChanged(LocalDate.now())
        viewModel.onPropertySaleStateChanged(EstateSaleState.ALL)

        // When
        viewModel.onResetFilters()
        viewModel.viewState.observeForTesting(this) {
            // Then
            assertEquals(testEmptyViewState, it.value)
        }
    }

    private val amenityTypes = AmenityType.values().toList()


    private val testFilterViewState: FilterViewState = FilterViewState(
        estateType = R.string.type_house,
        minPrice = "100000",
        maxPrice = "200000",
        minSurface = "100",
        maxSurface = "200",
        amenities =
        amenityTypes.map { amenityType ->
            AmenityViewState.AmenityCheckbox(
                id = amenityType.id,
                name = amenityType.name,
                isChecked = false,
                onCheckBoxClicked = EquatableCallbackWithParam { },
                iconDrawable = amenityType.iconDrawable,
                stringRes = amenityType.stringRes,
            )
        },
        estateTypes = getTestEstateTypesForFilter().map {
            TypeViewStateItem(
                id = it.id,
                name = NativeText.Resource(it.stringRes),
                databaseName = it.databaseName
            )
        },
        researchDate = LocalDate.now(),
        availableForSale = EstateSaleState.ALL,
        onFilterClicked = EquatableCallback { },
        onCancelClicked = EquatableCallback { },
        address = "",
        minMedia = ""
    )

    private val testEmptyViewState: FilterViewState = FilterViewState(
        estateType = null,
        minPrice = "",
        maxPrice = "",
        minSurface = "",
        maxSurface = "",
        amenities = amenityTypes.map { amenityType ->
            AmenityViewState.AmenityCheckbox(
                id = amenityType.id,
                name = amenityType.name,
                isChecked = false,
                onCheckBoxClicked = EquatableCallbackWithParam { },
                iconDrawable = amenityType.iconDrawable,
                stringRes = amenityType.stringRes,
            )
        },
        estateTypes = getTestEstateTypesForFilter().map {
            TypeViewStateItem(
                id = it.id,
                name = NativeText.Resource(it.stringRes),
                databaseName = it.databaseName
            )
        },
        researchDate = null,
        availableForSale = null,
        onFilterClicked = EquatableCallback { },
        onCancelClicked = EquatableCallback { },
        address = "",
        minMedia = "",
    )
}