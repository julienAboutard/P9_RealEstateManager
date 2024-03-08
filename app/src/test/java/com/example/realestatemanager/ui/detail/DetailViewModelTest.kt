package com.example.realestatemanager.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.realestatemanager.fixtures.getTestEstateEntity
import com.example.realestatemanager.R
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateUseCase
import com.example.realestatemanager.domain.estate.type.GetStringResourceForEstateTypeUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.map_picture.GenerateMapBaseUrlWithParamsUseCase
import com.example.realestatemanager.domain.map_picture.GenerateMapUrlWithApiKeyUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.detail.DetailViewModel
import com.example.realestatemanager.ui.estate.detail.DetailViewState
import com.example.realestatemanager.ui.estate.detail.medialist.MediaViewState
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText
import com.example.utils.TestCoroutineRule
import com.example.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale


class DetailViewModelTest {

    companion object {
        private val US = Locale.US
        private const val TEST_PROPERTY_ID = 1L
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getCurrentEstateUseCase: GetCurrentEstateUseCase = mockk()
    private val formatPriceToHumanReadableUseCase: FormatPriceToHumanReadableUseCase = mockk()
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase = mockk()
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase = mockk()
    private val formatAndRoundSurfaceToHumanReadableUseCase: FormatAndRoundSurfaceToHumanReadableUseCase = mockk()
    private val generateMapUrlWithApiKeyUseCase: GenerateMapUrlWithApiKeyUseCase = mockk()
    private val getStringResourceForEstateTypeUseCase: GetStringResourceForEstateTypeUseCase = mockk()
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase = mockk()
    private val generateMapBaseUrlWithParamsUseCase: GenerateMapBaseUrlWithParamsUseCase = mockk()

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        Locale.setDefault(US)
        every { getCurrentEstateUseCase.invoke() } returns flowOf(getTestEstateEntity(TEST_PROPERTY_ID))
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal(1000000)) } returns BigDecimal(1000000)
        every { formatPriceToHumanReadableUseCase.invoke(BigDecimal(1000000)) } returns "$1,000,000"
        every { formatAndRoundSurfaceToHumanReadableUseCase.invoke(BigDecimal(500)) } returns "500 m²"
        coEvery { convertToSquareMeterDependingOnLocaleUseCase.invoke(BigDecimal(500)) } returns BigDecimal(500)
        every { getStringResourceForEstateTypeUseCase.invoke("House") } returns R.string.type_house
        every { generateMapUrlWithApiKeyUseCase.invoke(any()) } returns "https://www.google.com/maps/123456789"
        every { generateMapBaseUrlWithParamsUseCase.invoke(any(), any()) } returns ""
        justRun { setNavigationTypeUseCase.invoke(any()) }

        viewModel = DetailViewModel(
            getCurrentEstateUseCase,
            formatPriceToHumanReadableUseCase,
            convertToEuroDependingOnLocaleUseCase,
            convertToSquareMeterDependingOnLocaleUseCase,
            formatAndRoundSurfaceToHumanReadableUseCase,
            getStringResourceForEstateTypeUseCase,
            setNavigationTypeUseCase,
            generateMapBaseUrlWithParamsUseCase,
            generateMapUrlWithApiKeyUseCase,
        )
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // When
        viewModel.viewState.observeForTesting(this) {

            // Then
            assertThat(it.value).isEqualTo(testDetailViewStateDetails)
        }
    }

    @Test
    fun `on edit clicked`() {
        // When
        viewModel.onEditClicked()

        // Then
        verify(exactly = 1) { setNavigationTypeUseCase.invoke(NavigationFragmentType.EDIT_FRAGMENT) }
    }

    private val testDetailViewStateDetails = DetailViewState(
        id = TEST_PROPERTY_ID,
        estateType = R.string.type_house,
        medias = listOf(
            MediaViewState(
                mediaUri = NativePhoto.Uri("https://www.google.com/front_view"),
                description = "Front view",
                type = "pic"

                ),
            MediaViewState(
                mediaUri = NativePhoto.Uri("https://www.google.com/garden"),
                description = "Garden",
                type = "vid"

                ),
            MediaViewState(
                mediaUri = NativePhoto.Uri("https://www.google.com/swimming_pool"),
                description = "Swimming pool",
                type = "pic"
            ),
        ),
        mapMiniature = NativePhoto.Uri("https://www.google.com/maps/123456789"),
        price = "$1,000,000",
        surface = "500 m²",
        rooms = NativeText.Argument(R.string.detail_number_of_room_textview, 5),
        bathrooms = NativeText.Argument(R.string.detail_number_of_bathroom_textview, 2),
        bedrooms = NativeText.Argument(R.string.detail_number_of_bedroom_textview, 3),
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        address = NativeText.Argument(R.string.detail_location_tv, "1st, Dummy Street, 12345, Dummy City"),
        amenities = buildList<AmenityViewState.AmenityItem> {
            add(AmenityViewState.AmenityItem(R.string.amenity_school, R.drawable.baseline_school_24))
            add(AmenityViewState.AmenityItem(R.string.amenity_park, R.drawable.baseline_park_24))
            add(AmenityViewState.AmenityItem(R.string.amenity_shopping_mall, R.drawable.baseline_shopping_cart_24))
        },
        entryDate = NativeText.Argument(R.string.detail_entry_date_tv, "1/1/23"),
        agentName = NativeText.Argument(R.string.detail_manager_agent_name, "Shiro Almira"),
        isSold = false,
        saleDate = null,
    )
}
