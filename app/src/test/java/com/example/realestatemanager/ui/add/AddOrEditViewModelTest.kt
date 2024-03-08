package com.example.realestatemanager.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestatemanager.fixtures.getTestAgents
import com.example.realestatemanager.fixtures.getTestAmenities
import com.example.realestatemanager.fixtures.getTestEstateTypes
import com.example.realestatemanager.R
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import com.example.realestatemanager.data.formatting.type.CurrencyType
import com.example.realestatemanager.data.formatting.type.SurfaceUnitType
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.agent.GetRealEstateAgentsUseCase
import com.example.realestatemanager.domain.amenity.GetAmenityTypeUseCase
import com.example.realestatemanager.domain.autocomplete.GetCurrentPredictionAddressesFlowWithDebounceUseCase
import com.example.realestatemanager.domain.autocomplete.address.SetHasAddressFocusUseCase
import com.example.realestatemanager.domain.autocomplete.address.SetSelectedAddressStateUseCase
import com.example.realestatemanager.domain.autocomplete.address.UpdateOnAddressClickedUseCase
import com.example.realestatemanager.domain.connectivity.IsInternetEnabledFlowUseCase
import com.example.realestatemanager.domain.content_resolver.SaveFileToLocalAppFilesUseCase
import com.example.realestatemanager.domain.estate.AddOrEditEstateUseCase
import com.example.realestatemanager.domain.estate.GetEstateByIdUseCase
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateIdFlowUseCase
import com.example.realestatemanager.domain.estate.type.GetEstateTypeUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareFeetDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToUsdDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.GetCurrencyTypeUseCase
import com.example.realestatemanager.domain.formatting.GetSurfaceUnitUseCase
import com.example.realestatemanager.domain.navigation.GetNavigationTypeUseCase
import com.example.realestatemanager.fixtures.getTestEstateEntity
import com.example.realestatemanager.fixtures.getTestMediaEntities
import com.example.realestatemanager.ui.estate.add.AddOrEditEvent
import com.example.realestatemanager.ui.estate.add.AddOrEditViewModel
import com.example.realestatemanager.ui.estate.add.AddOrEditViewState
import com.example.realestatemanager.ui.estate.add.address_predictions.PredictionViewState
import com.example.realestatemanager.ui.estate.add.agent.AgentViewStateItem
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.media.MediaPreviewViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.Event
import com.example.realestatemanager.ui.utils.NativeText
import com.example.utils.TestCoroutineRule
import com.example.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

class AddOrEditViewModelTest {

    companion object {
        private const val TEST_ESTATE_ID = 1L
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getRealEstateAgentsUseCase: GetRealEstateAgentsUseCase = mockk()
    private val getCurrencyTypeUseCase: GetCurrencyTypeUseCase = mockk()
    private val getEstateTypeUseCase: GetEstateTypeUseCase = mockk()
    private val getAmenityTypeUseCase: GetAmenityTypeUseCase = mockk()
    private val getSurfaceUnitUseCase: GetSurfaceUnitUseCase = mockk()
    private val isInternetEnabledFlowUseCase: IsInternetEnabledFlowUseCase = mockk()
    private val addOrEditEstateUseCase: AddOrEditEstateUseCase = mockk()
    private val saveFileToLocalAppFilesUseCase: SaveFileToLocalAppFilesUseCase = mockk()
    private val getNavigationTypeUseCase: GetNavigationTypeUseCase = mockk()
    private val getCurrentEstateIdFlowUseCase: GetCurrentEstateIdFlowUseCase = mockk()
    private val getEstateByIdUseCase: GetEstateByIdUseCase = mockk()
    private val setSelectedAddressStateUseCase: SetSelectedAddressStateUseCase = mockk()
    private val updateOnAddressClickedUseCase: UpdateOnAddressClickedUseCase = mockk()
    private val setHasAddressFocusUseCase: SetHasAddressFocusUseCase = mockk()
    private val getCurrentPredictionAddressesFlowWithDebounceUseCase: GetCurrentPredictionAddressesFlowWithDebounceUseCase = mockk()
    private val convertToUsdDependingOnLocaleUseCase: ConvertToUsdDependingOnLocaleUseCase = mockk()
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase = mockk()
    private val convertToSquareFeetDependingOnLocaleUseCase: ConvertToSquareFeetDependingOnLocaleUseCase = mockk()
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase = mockk()

    private lateinit var viewModel: AddOrEditViewModel

    @Before
    fun setUp() {
        coEvery { addOrEditEstateUseCase.invoke(any()) } returns AddOrEditEvent.Toast(NativeText.Simple("Test"))
        coEvery { getCurrentEstateIdFlowUseCase.invoke() } returns flowOf(TEST_ESTATE_ID)
        coEvery { saveFileToLocalAppFilesUseCase.invoke(any(), any()) } returns "prefix123456suffix"
        every { getRealEstateAgentsUseCase.invoke() } returns getTestAgents()
        every { getCurrencyTypeUseCase.invoke() } returns CurrencyType.DOLLAR
        coEvery { convertToUsdDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal.ZERO
        every { convertToSquareMeterDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal.ZERO
        coEvery { convertToEuroDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal.ZERO
        every { convertToSquareFeetDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal.ZERO
        every { getSurfaceUnitUseCase.invoke() } returns SurfaceUnitType.SQUARE_FOOT
        coJustRun { updateOnAddressClickedUseCase.invoke(any()) }
        justRun { setHasAddressFocusUseCase.invoke(any()) }
        coEvery { getEstateTypeUseCase.invoke() } returns getTestEstateTypes()
        every { getNavigationTypeUseCase.invoke() } returns flowOf(NavigationFragmentType.SLIDING_FRAGMENT)
        coEvery { getCurrentPredictionAddressesFlowWithDebounceUseCase.invoke() } returns flowOf(
            testPredictionSuccessWrapper
        )
        every { getAmenityTypeUseCase.invoke() } returns getTestAmenities()
        coEvery { isInternetEnabledFlowUseCase.invoke() } returns flowOf(true)
        coEvery { getEstateByIdUseCase.invoke(any()) } returns getTestEstateEntity(1L)


        viewModel = AddOrEditViewModel(
            getRealEstateAgentsUseCase,
            getCurrencyTypeUseCase,
            getEstateTypeUseCase,
            getAmenityTypeUseCase,
            getSurfaceUnitUseCase,
            isInternetEnabledFlowUseCase,
            addOrEditEstateUseCase,
            saveFileToLocalAppFilesUseCase,
            getNavigationTypeUseCase,
            getCurrentEstateIdFlowUseCase,
            getEstateByIdUseCase,
            setSelectedAddressStateUseCase,
            updateOnAddressClickedUseCase,
            setHasAddressFocusUseCase,
            getCurrentPredictionAddressesFlowWithDebounceUseCase,
            convertToUsdDependingOnLocaleUseCase,
            convertToSquareFeetDependingOnLocaleUseCase,
            convertToSquareMeterDependingOnLocaleUseCase,
            convertToEuroDependingOnLocaleUseCase,
        )
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // When
            advanceTimeBy(400)
            runCurrent()

            // Then
            assertEquals(testInitialViewState, it.value)

            coVerify { getCurrentEstateIdFlowUseCase.invoke() }
            coVerify { getEstateByIdUseCase.invoke(any()) }

            confirmVerified(
                getCurrentEstateIdFlowUseCase,
                getEstateByIdUseCase,
            )
        }
    }

    @Test
    fun `on add property clicked - triggers submit button event`() = testCoroutineRule.runTest {
        viewModel.viewEventLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onAddPropertyClicked()
            runCurrent()

            // Then
            assertEquals(Event(AddOrEditEvent.Toast(NativeText.Simple("Test"))), it.value)
            coVerify(exactly = 1) { addOrEditEstateUseCase.invoke(any()) }
            confirmVerified(addOrEditEstateUseCase)
        }
    }

    @Test
    fun `when clicking on address selection - set it as selected address`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            it.value!!.addressPredictions[2].onClickEvent.invoke(
                testPredictionSuccessWrapper.predictions[2]
            )
            runCurrent()

            // Then
            assertEquals(
                testPredictionSuccessWrapper.predictions[2],
                it.value!!.address
            )
            coVerify { updateOnAddressClickedUseCase.invoke(any()) }
        }
    }

    @Test
    fun `on type changed - type is set`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onTypeSelected(getTestEstateTypes()[1].databaseName)
            runCurrent()

            // Then
            assertEquals(
                getTestEstateTypes().get(1).stringRes,
                it.value!!.type
            )
        }
    }

    @Test
    fun `on agent changed - agent is set`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onAgentSelected(getTestAgents()[1].agentName)
            runCurrent()

            // Then
            assertEquals(
                getTestAgents()[1].agentName,
                it.value!!.selectedAgent
            )
        }
    }


    @Test
    fun `on number of rooms changed - number of rooms is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onRoomsNumberChanged("10")
            runCurrent()

            // Then
            assertEquals("10", it.value!!.nbRooms)
        }
    }

    @Test
    fun `on number of bedrooms changed - number of bedrooms is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onBedroomsNumberChanged("4")
            runCurrent()

            // Then
            assertEquals("4", it.value!!.nbBedrooms)
        }
    }

    @Test
    fun `on number of bathrooms changed - number of bathrooms is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onBathroomsNumberChanged("6")
            runCurrent()

            // Then
            assertEquals("6", it.value!!.nbBathrooms)
        }
    }

    @Test
    fun `on surface changed - surface is set`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onSurfaceChanged("100")
            runCurrent()

            // Then
            assertEquals(
                "100",
                it.value!!.surface
            )
        }
    }


    @Test
    fun `on amenities checked - amenities is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(true)
            runCurrent()

            // Then
            assertTrue(
                (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).isChecked
            )
            assertFalse(
                (it.value!!.amenities[3] as AmenityViewState.AmenityCheckbox).isChecked
            )
        }
    }

    @Test
    fun `on 3 amenities checked - 3 amenities are updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(true)
            (it.value!!.amenities[3] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(true)
            (it.value!!.amenities[5] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(true)
            runCurrent()

            // Then
            assertTrue(
                (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).isChecked
            )
            assertTrue(
                (it.value!!.amenities[3] as AmenityViewState.AmenityCheckbox).isChecked
            )
            assertTrue(
                (it.value!!.amenities[5] as AmenityViewState.AmenityCheckbox).isChecked
            )
        }
    }

    @Test
    fun `on amenities check toggled - amenities is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(true)
            (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).onCheckBoxClicked.invoke(false)
            runCurrent()

            // Then
            assertFalse(
                (it.value!!.amenities[2] as AmenityViewState.AmenityCheckbox).isChecked
            )
        }
    }

    @Test
    fun `on sold status changed - sold status is updated`() = testCoroutineRule.runTest {
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            viewModel.onSoldStatusChanged(true)
            runCurrent()

            // Then
            assertEquals(true, it.value!!.isSold)
        }
    }

    @Test
    fun `set currency type to EURO - should display prices in EURO`() = testCoroutineRule.runTest {
        every { getCurrencyTypeUseCase.invoke() } returns CurrencyType.EURO
        viewModel.viewStateLiveData.observeForTesting(this) {
            // Given
            advanceTimeBy(400)
            runCurrent()

            // When
            assertEquals(
                NativeText.Argument(R.string.price_hint, "€"),
                it.value!!.priceCurrencyHint
            )
            assertEquals(
                R.drawable.baseline_euro_24,
                it.value!!.currencyDrawableRes
            )
        }
    }


    @Test
    fun `internet is disabled - address validity isn't checked + empty suggestions`() = testCoroutineRule.runTest {
        // Given
        coEvery { isInternetEnabledFlowUseCase.invoke() } returns flowOf(false)


        viewModel.viewStateLiveData.observeForTesting(this) {
            advanceTimeBy(400)
            runCurrent()

            // Then
            assertFalse(it.value!!.isInternetEnabled)
            assertEquals(
                emptyList<PredictionViewState>(),
                it.value!!.addressPredictions
            )
        }
    }

    @Test
    fun `when delete media preview - update list of media`() = testCoroutineRule.runTest {
        // Given

        // When
        viewModel.viewStateLiveData.observeForTesting(this) {
            advanceTimeBy(400)
            runCurrent()

            println(it.value)
            it.value!!.medias[0].onDeleteEvent.invoke()
            runCurrent()

            // Then
            assertEquals(2, it.value!!.medias.size)
        }
    }

    /* private fun caseFormFilled() {
         coEvery { initPropertyFormUseCase.invoke(TEST_ESTATE_ID) } returns testFilledFormTypeEntity
         coEvery { isFormCompletedAsFlowUseCase.invoke() } returns flowOf(true)
         justRun { setFormCompletionUseCase.invoke(any()) }
         justRun { setFormTitleUseCase.invoke(any(), any()) }
         coEvery { convertToUsdDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal(100000)
         every { convertToSquareMeterDependingOnLocaleUseCase.invoke(any()) } returns BigDecimal(500)
     }*/


    private val testPredictionSuccessWrapper = PredictionWrapper.Success(
        listOf(
            "1st, Dummy Street, 12345, Dummy City",
            "2nd, Dummy Street, 12345, Dummy City",
            "3rd, Dummy Street, 12345, Dummy City",
            "4th, Dummy Street, 12345, Dummy City",
            "5th, Dummy Street, 12345, Dummy City",
        )
    )

    private val testInitialViewState = AddOrEditViewState(
        type = R.string.type_house,
        addressPredictions = listOf(
            PredictionViewState(
                address = "1st, Dummy Street, 12345, Dummy City",
                onClickEvent = EquatableCallbackWithParam { }
            ),
            PredictionViewState(
                address = "2nd, Dummy Street, 12345, Dummy City",
                onClickEvent = EquatableCallbackWithParam { }
            ),
            PredictionViewState(
                address = "3rd, Dummy Street, 12345, Dummy City",
                onClickEvent = EquatableCallbackWithParam { }
            ),
            PredictionViewState(
                address = "4th, Dummy Street, 12345, Dummy City",
                onClickEvent = EquatableCallbackWithParam { }
            ),
            PredictionViewState(
                address = "5th, Dummy Street, 12345, Dummy City",
                onClickEvent = EquatableCallbackWithParam { }
            ),
        ),
        isAddressValid = false,
        address = "1st, Dummy Street, 12345, Dummy City",
        price = "",
        surface = "",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        nbRooms = "5",
        nbBathrooms = "2",
        nbBedrooms = "2",
        medias = getTestMediaEntities().map {
            MediaPreviewViewState(
                it.id,
                it.type,
                it.uri,
                it.isFeatured,
                it.description,
                onDeleteEvent = EquatableCallback{},
                onFeaturedEvent = EquatableCallbackWithParam {  },
                onDescriptionChanged = EquatableCallbackWithParam {  }
            )
        },
        agents = getTestAgents().map { AgentViewStateItem(it.id, it.agentName) },
        selectedAgent = "Shiro Almira",
        priceCurrencyHint = NativeText.Argument(
            R.string.price_hint,
            "$"
        ),
        currencyDrawableRes = R.drawable.baseline_dollar_24,
        surfaceUnit = NativeText.Argument(
            R.string.surface_area_unit_in_n,
            "sq ft",
        ),
        submitButtonText = NativeText.Resource(R.string.form_update_button),
        isProgressBarVisible = false,
        amenities = getTestAmenities().map {
            AmenityViewState.AmenityCheckbox(
                id = it.id,
                name = it.name,
                isChecked = it.name == "SCHOOL" || it.name == "PARK" || it.name == "SHOPPING_MALL",
                onCheckBoxClicked = EquatableCallbackWithParam { },
                iconDrawable = it.iconDrawable,
                stringRes = it.stringRes,
            )
        },
        estateTypes = getTestEstateTypes().map {
            TypeViewStateItem(
                id = it.id,
                name = NativeText.Resource(it.stringRes),
                databaseName = it.databaseName,
            )
        },
        entryDate = "01/01/2023",
        isSold = false,
        soldDate = null,
        areEditItemsVisible = true,
        isInternetEnabled = true,
    )
}