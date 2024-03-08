package com.example.realestatemanager.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.realestatemanager.R
import com.example.realestatemanager.data.filter.EstatesFilterEntity
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.fixtures.getEstateEntities
import com.example.realestatemanager.domain.estate.GetEstatesAsFlowUseCase
import com.example.realestatemanager.domain.estate.current.ResetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.estate.current.SetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.estate.type.GetStringResourceForEstateTypeUseCase
import com.example.realestatemanager.domain.filter.GetEstatesFilterFlowUseCase
import com.example.realestatemanager.domain.filter.IsEstateMatchingFiltersUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.list.ListEstateViewModel
import com.example.realestatemanager.ui.estate.list.ListEstateViewState
import com.example.realestatemanager.ui.utils.EquatableCallback
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

class ListEstateViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getEstatesAsFlowUseCase: GetEstatesAsFlowUseCase = mockk()
    private val setCurrentEstateIdUseCase: SetCurrentEstateIdUseCase = mockk()
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase = mockk()
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase = mockk()
    private val getRoundedHumanReadableSurfaceUseCase: FormatAndRoundSurfaceToHumanReadableUseCase = mockk()
    private val formatPriceToHumanReadableUseCase: FormatPriceToHumanReadableUseCase = mockk()
    private val getEstatesFilterFlowUseCase: GetEstatesFilterFlowUseCase = mockk()
    private val getStringResourceForEstateTypeUseCase: GetStringResourceForEstateTypeUseCase = mockk()
    private val isEstateMatchingFiltersUseCase: IsEstateMatchingFiltersUseCase = mockk()
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase = mockk()
    private val resetCurrentEstateIdUseCase: ResetCurrentEstateIdUseCase = mockk()

    private lateinit var viewModel: ListEstateViewModel

    @Before
    fun setUp() {
        justRun { setCurrentEstateIdUseCase.invoke(any()) }
        every { convertToSquareMeterDependingOnLocaleUseCase.invoke(BigDecimal(500)) } returns BigDecimal(500)
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal(1000000)) } returns BigDecimal(1000000)
        every { getRoundedHumanReadableSurfaceUseCase.invoke(BigDecimal(500)) } returns "500 sq ft"
        every { formatPriceToHumanReadableUseCase.invoke(BigDecimal(1000000)) } returns "$1,000,000"
        every { getEstatesFilterFlowUseCase.invoke() } returns flowOf(null)
        every { getStringResourceForEstateTypeUseCase.invoke("House") } returns R.string.type_house
        every { getStringResourceForEstateTypeUseCase.invoke("Villa") } returns R.string.type_villa
        every { isEstateMatchingFiltersUseCase.invoke(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns true
        justRun { setNavigationTypeUseCase.invoke(any()) }

        viewModel = ListEstateViewModel(
            getEstatesAsFlowUseCase,
            setCurrentEstateIdUseCase,
            getStringResourceForEstateTypeUseCase,
            convertToSquareMeterDependingOnLocaleUseCase,
            convertToEuroDependingOnLocaleUseCase,
            getRoundedHumanReadableSurfaceUseCase,
            formatPriceToHumanReadableUseCase,
            getEstatesFilterFlowUseCase,
            setNavigationTypeUseCase,
            isEstateMatchingFiltersUseCase,
            resetCurrentEstateIdUseCase,

        )
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // When
        coEvery { getEstatesAsFlowUseCase.invoke() } returns flowOf(emptyList())

        // Then
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(emptyList())
        }
    }

    @Test
    fun `3 properties give 3 view states`() = testCoroutineRule.runTest {
        coEvery { getEstatesAsFlowUseCase.invoke() } returns flowOf(getEstateEntities(3))
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value!!.size).isEqualTo(3)
            assertThat(it.value).isEqualTo(testViewStates)
        }
    }

    @Test
    fun `4 properties with price filtering give 1 view states`() = testCoroutineRule.runTest {
        val testPropertyEntity = getEstateEntities(3).first().copy(
            price = BigDecimal(2000000),
        )
        coEvery { getEstatesAsFlowUseCase.invoke() } returns flowOf(getEstateEntities(3) + testPropertyEntity)
        every { getEstatesFilterFlowUseCase.invoke() } returns flowOf(
            EstatesFilterEntity(
                researchDate = null,
                minMaxPrice = Pair(
                    BigDecimal(1500000),
                    BigDecimal(2000000)
                )
            )
        )
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(BigDecimal(2000000)) } returns BigDecimal(2000000)
        every { getRoundedHumanReadableSurfaceUseCase.invoke(BigDecimal(500)) } returns "500 sq ft"
        every { formatPriceToHumanReadableUseCase.invoke(BigDecimal(2000000)) } returns "$2,000,000"
        every {
            isEstateMatchingFiltersUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns false andThen false andThen false andThen true
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value!!.size).isEqualTo(1)
            assertThat((it.value!![0] as ListEstateViewState).price).isEqualTo("$2,000,000")
        }
    }

    @Test
    fun `5 properties with type filtering give 2 view states`() = testCoroutineRule.runTest {
        // Given
        val testPropertyEntity = getEstateEntities(3).first().copy(
            type = "Villa",
        )
        val testPropertyEntity2 = getEstateEntities(3)[1].copy(
            type = "Villa",
        )
        coEvery { getEstatesAsFlowUseCase.invoke() } returns flowOf(getEstateEntities(3) + testPropertyEntity + testPropertyEntity2)
        every { getEstatesFilterFlowUseCase.invoke() } returns flowOf(EstatesFilterEntity(estateType = "Villa", researchDate = null))

        every {
            isEstateMatchingFiltersUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns false andThen false andThen false andThen true andThen true

        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value!!.size).isEqualTo(2)
            assertThat((it.value!![0] as ListEstateViewState).estateType).isEqualTo(R.string.type_villa)
            assertThat((it.value!![1] as ListEstateViewState).estateType).isEqualTo(R.string.type_villa)
        }
    }

    @Test
    fun `on click event should set both current property id and navigation`() = testCoroutineRule.runTest {
        // Given
        coEvery { getEstatesAsFlowUseCase.invoke() } returns flowOf(getEstateEntities(3))
        viewModel.viewState.observeForTesting(this) {
            // When
            (it.value!![0] as ListEstateViewState).onClickEvent.invoke()
            // Then
            verify(exactly = 1) {
                setCurrentEstateIdUseCase.invoke(1L)
            }
        }
    }


    private val testViewStates = buildList {
        getEstateEntities(3).forEach {
            add(ListEstateViewState(
                id = it.id,
                estateType = R.string.type_house,
                featuredPicture = it.medias.firstOrNull()?.let { featuredPic -> NativePhoto.Uri(featuredPic.uri) }
                    ?: NativePhoto.Resource(R.drawable.baseline_add_home_24),
                location = it.location,
                price = "$1,000,000",
                isSold = false,
                room = NativeText.Argument(R.string.detail_number_of_room_textview, it.rooms),
                bathroom = NativeText.Argument(com.example.realestatemanager.R.string.detail_number_of_bathroom_textview, it.bathrooms),
                bedroom = NativeText.Argument(com.example.realestatemanager.R.string.detail_number_of_bedroom_textview, it.bedrooms),
                surface = "500 sq ft",
                entryDate = it.entryDate,
                amenities = it.amenities,
                onClickEvent = EquatableCallback {},
            )
            )
        }
    }

}