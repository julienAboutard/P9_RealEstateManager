package com.example.realestatemanager.ui.estate.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.realestatemanager.R
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.data.media.entity.MediaEntity
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
import com.example.realestatemanager.domain.form.FormParams
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareFeetDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToUsdDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.GetCurrencyTypeUseCase
import com.example.realestatemanager.domain.formatting.GetSurfaceUnitUseCase
import com.example.realestatemanager.domain.navigation.GetNavigationTypeUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.address_predictions.PredictionViewState
import com.example.realestatemanager.ui.estate.add.agent.AgentViewStateItem
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.media.MediaPreviewViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.Event
import com.example.realestatemanager.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
    private val getRealEstateAgents: GetRealEstateAgentsUseCase,
    private val getCurrencyTypeUseCase: GetCurrencyTypeUseCase,
    private val getEstateTypeUseCase: GetEstateTypeUseCase,
    private val getAmenityTypeUseCase: GetAmenityTypeUseCase,
    private val getSurfaceUnitUseCase: GetSurfaceUnitUseCase,
    private val isInternetEnabledFlowUseCase: IsInternetEnabledFlowUseCase,
    private val addOrEditEstateUseCase: AddOrEditEstateUseCase,
    private val saveFileToLocalAppFilesUseCase: SaveFileToLocalAppFilesUseCase,
    private val getNavigationTypeUseCase: GetNavigationTypeUseCase,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val getCurrentEstateIdFlowUseCase: GetCurrentEstateIdFlowUseCase,
    private val getEstateByIdUseCase: GetEstateByIdUseCase,
    private val setSelectedAddressStateUseCase: SetSelectedAddressStateUseCase,
    private val updateOnAddressClickedUseCase: UpdateOnAddressClickedUseCase,
    private val setHasAddressFocusUseCase: SetHasAddressFocusUseCase,
    private val getCurrentPredictionAddressesFlowWithDebounceUseCase: GetCurrentPredictionAddressesFlowWithDebounceUseCase,
    private val convertToUsdDependingOnLocaleUseCase: ConvertToUsdDependingOnLocaleUseCase,
    private val convertToSquareFeetDependingOnLocaleUseCase: ConvertToSquareFeetDependingOnLocaleUseCase,
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase,
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase,

    ) : ViewModel() {

    private val formMutableStateFlow = MutableStateFlow(FormParams())
    private val onSubmitButtonClickedMutableSharedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val isLoadingMutableStateFlow = MutableStateFlow(true)

    val viewEventLiveData: LiveData<Event<AddOrEditEvent>> = liveData {
        coroutineScope {
            launch {
                isLoadingMutableStateFlow.collect { isLoading ->
                    if (isLoading) emit(
                        Event(
                            AddOrEditEvent.Loading
                        )
                    ) else emit(
                        Event(
                            AddOrEditEvent.Form
                        )
                    )
                }
            }

            launch {
                onSubmitButtonClickedMutableSharedFlow.collectLatest {
                    emit(
                        Event(
                            addOrEditEstateUseCase.invoke(formMutableStateFlow.value)
                        )
                    )
                }
            }
        }
    }

    val viewStateLiveData: LiveData<AddOrEditViewState> = liveData {
        coroutineScope {
            if (latestValue == null) {
                isLoadingMutableStateFlow.tryEmit(true)
                delay(400)
            }

            val estateId = getCurrentEstateIdFlowUseCase.invoke().firstOrNull()
            if (estateId != null) {
                val estate = getEstateByIdUseCase.invoke(estateId)
                formMutableStateFlow.update {
                    it.copy(
                        id = estate.id,
                        addOrEditStatus = "edit",
                        type = estate.type,
                        address = estate.location,
                        isAddressValid = (estate.latitude != null && estate.longitude != null),
                        price = convertToEuroDependingOnLocaleUseCase.invoke(estate.price),
                        surface = convertToSquareMeterDependingOnLocaleUseCase.invoke(estate.surface),
                        description = estate.description,
                        rooms = estate.rooms,
                        bathrooms = estate.bathrooms,
                        bedrooms = estate.bathrooms,
                        agent = estate.agentName,
                        selectedAmenities = estate.amenities,
                        mediasInit = estate.medias,
                        medias = estate.medias,
                        isSold = estate.saleDate != null,
                        entryDate = estate.entryDate,
                        soldDate = estate.saleDate,
                    )
                }
            }

            launch {
                combine(
                    formMutableStateFlow,
                    isInternetEnabledFlowUseCase.invoke(),
                    getCurrentPredictionAddressesFlowWithDebounceUseCase.invoke()
                        .onStart { emit(null) },
                ) { form, isInternetEnabled, predictionWrapper->

                    isLoadingMutableStateFlow.tryEmit(false)

                    val currencyType = getCurrencyTypeUseCase.invoke()
                    val amenityTypes = getAmenityTypeUseCase.invoke()
                    val estateTypes = getEstateTypeUseCase.invoke()
                    val agents = getRealEstateAgents.invoke()

                    AddOrEditViewState(
                        type = estateTypes.find { it.databaseName == form.type }?.stringRes,
                        address = form.address,
                        price = if (form.price == BigDecimal.ZERO) "" else form.price.toString(),
                        surface = if (form.surface == BigDecimal.ZERO) "" else form.surface.toString(),
                        description = form.description,
                        nbRooms = if (form.rooms == 0) "" else form.rooms.toString(),
                        nbBathrooms = if (form.bathrooms == 0) "" else form.bathrooms.toString(),
                        nbBedrooms = if (form.bedrooms == 0) "" else form.bedrooms.toString(),
                        medias = mapMediaPreviews(form.medias.sortedBy { it.id }, form),
                        selectedAgent = form.agent,
                        priceCurrencyHint = NativeText.Argument(
                            R.string.price_hint,
                            currencyType.symbol,
                        ),
                        currencyDrawableRes = currencyType.drawableRes,
                        surfaceUnit = NativeText.Argument(
                            R.string.surface_area_unit_in_n,
                            getSurfaceUnitUseCase.invoke().symbol,
                        ),
                        submitButtonText = if (form.addOrEditStatus == "add") NativeText.Resource(R.string.form_create_button) else
                            NativeText.Resource(R.string.form_update_button),
                        isProgressBarVisible = false,
                        amenities = mapAmenityTypesToViewStates(amenityTypes),
                        estateTypes = estateTypes
                            .filterNot { it.id == 0L }
                            .map { estateType ->
                                TypeViewStateItem(
                                    id = estateType.id,
                                    name = NativeText.Resource(estateType.stringRes),
                                    databaseName = estateType.databaseName
                                )
                            },
                        agents = agents.map { agent ->
                            AgentViewStateItem(
                                id = agent.id,
                                name = agent.agentName,
                            )
                        },
                        addressPredictions = mapPredictionsToViewState(predictionWrapper, isInternetEnabled),
                        isSold = form.isSold,
                        soldDate = form.soldDate?.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.SHORT
                            )
                        ),
                        entryDate = form.entryDate?.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.SHORT
                            )
                        ),
                        isAddressValid = false,
                        areEditItemsVisible = form.addOrEditStatus == "edit",
                        isInternetEnabled = isInternetEnabled,
                    )
                }.collectLatest {
                    emit(it)
                }
            }
        }
    }


    private fun mapAmenityTypesToViewStates(amenityTypes: List<AmenityType>): List<AmenityViewState> =
        amenityTypes.map { amenityType ->
            AmenityViewState.AmenityCheckbox(
                id = amenityType.id,
                name = amenityType.name,
                iconDrawable = amenityType.iconDrawable,
                stringRes = amenityType.stringRes,
                isChecked = formMutableStateFlow.value.selectedAmenities.contains(amenityType),
                onCheckBoxClicked = EquatableCallbackWithParam { isChecked ->
                    if (formMutableStateFlow.value.selectedAmenities.contains(amenityType) && !isChecked) {
                        formMutableStateFlow.update {
                            it.copy(selectedAmenities = it.selectedAmenities - amenityType)
                        }
                    } else if (!formMutableStateFlow.value.selectedAmenities.contains(amenityType) && isChecked) {
                        formMutableStateFlow.update {
                            it.copy(selectedAmenities = it.selectedAmenities + amenityType)
                        }
                    }
                },
            )
        }

    private fun mapMediaPreviews(
        mediaEntities: List<MediaEntity>,
        form: FormParams
    ): List<MediaPreviewViewState> =
        mediaEntities.map { mediaEntity ->
            MediaPreviewViewState(
                id = mediaEntity.id,
                type = mediaEntity.type,
                uri = mediaEntity.uri,
                isFeatured = mediaEntity.isFeatured,
                description = mediaEntity.description,
                onDeleteEvent = EquatableCallback {
                    formMutableStateFlow.update {
                        it.copy(medias = it.medias - mediaEntity)
                    }
                },
                onFeaturedEvent = EquatableCallbackWithParam { isFeatured ->
                    if (isFeatured) {
                        val medias = form.medias.find { it.isFeatured }
                        if (medias == null) {
                            formMutableStateFlow.update {
                                it.copy(medias = it.medias - mediaEntity)
                            }

                            mediaEntity.isFeatured = true
                            formMutableStateFlow.update {
                                it.copy(medias = it.medias + mediaEntity)
                            }
                        }
                    } else {
                        formMutableStateFlow.update {
                            it.copy(medias = it.medias - mediaEntity)
                        }

                        mediaEntity.isFeatured = false
                        formMutableStateFlow.update {
                            it.copy(medias = it.medias + mediaEntity)
                        }
                    }

                },
                onDescriptionChanged = EquatableCallbackWithParam { description ->
                    formMutableStateFlow.update {
                        it.copy(medias = it.medias - mediaEntity)
                    }
                    mediaEntity.description = description
                    formMutableStateFlow.update {
                        it.copy(medias = it.medias + mediaEntity)
                    }
                },
            )
        }

    private fun mapPredictionsToViewState(
        currentPredictionAddresses: PredictionWrapper?,
        isInternetEnabled: Boolean
    ): List<PredictionViewState> =
        if (isInternetEnabled) {
            when (currentPredictionAddresses) {
                is PredictionWrapper.Success -> {
                    currentPredictionAddresses.predictions.map { prediction ->
                        PredictionViewState(
                            address = prediction,
                            onClickEvent = EquatableCallbackWithParam { selectedAddress ->
                                formMutableStateFlow.update {
                                    it.copy(
                                        address = selectedAddress,
                                        isAddressValid = true
                                    )
                                }
                                viewModelScope.launch {
                                    updateOnAddressClickedUseCase.invoke(
                                        true
                                    )
                                }
                            }
                        )
                    }
                }

                is PredictionWrapper.NoResult -> emptyList()
                is PredictionWrapper.Error -> emptyList<PredictionViewState>().also { println("Error: ${currentPredictionAddresses.error}") }
                is PredictionWrapper.Failure -> emptyList<PredictionViewState>().also {
                    println("Failure: ${currentPredictionAddresses.failure}")
                }

                else -> emptyList()
            }
        } else {
            formMutableStateFlow.update {
                it.copy(
                    isAddressValid = false // we can't say for sure if the address is valid without internet
                )
            }
            emptyList()
        }

    fun onTypeSelected(type: String) {
        formMutableStateFlow.update {
            it.copy(type = type)
        }
    }

    fun onAgentSelected(agent: String) {
        formMutableStateFlow.update {
            it.copy(agent = agent)
        }
    }

    fun onPictureSelected(stringUri: String) {
        viewModelScope.launch {
            val urlPath = saveFileToLocalAppFilesUseCase.invoke(stringUri, ".jpg")
                ?: "android.resource://com.example.realestatemanager/${R.drawable.baseline_add_home_24}"

            formMutableStateFlow.update {
                val mediaEntity = MediaEntity(
                    id = 0L,
                    uri = urlPath,
                    description = null,
                    isFeatured = false,
                    type = "pic"
                )

                if (it.medias.isEmpty()) {
                    it.copy(
                        medias = listOf(mediaEntity),

                        )
                } else {
                    it.copy(medias = formMutableStateFlow.value.medias + mediaEntity)
                }
            }
        }
    }

    fun onVideoSelected(stringUri: String) {
        viewModelScope.launch {
            val urlPath = saveFileToLocalAppFilesUseCase.invoke(stringUri, ".mp4")
                ?: "android.resource://com.example.realestatemanager/${R.drawable.baseline_add_home_24}"

            formMutableStateFlow.update {
                val mediaEntity = MediaEntity(
                    id = 0L,
                    uri = urlPath,
                    description = null,
                    isFeatured = false,
                    type = "vid"
                )

                if (it.medias.isEmpty()) {
                    it.copy(
                        medias = listOf(mediaEntity),
                        )
                } else {
                    it.copy(medias = formMutableStateFlow.value.medias + mediaEntity)
                }
            }
        }
    }

    fun onDescriptionChanged(description: String) {
        formMutableStateFlow.update {
            it.copy(description = description.ifBlank { null })
        }
    }

    fun onAddressChanged(input: String?) {
        viewModelScope.launch {
            isInternetEnabledFlowUseCase.invoke().firstOrNull()?.let { isInternetEnabled ->
                if (!isInternetEnabled) {
                    formMutableStateFlow.update {
                        it.copy(
                            address = input,
                        )
                    }
                    return@launch
                }
            }
        }

        if (formMutableStateFlow.value.isAddressValid && formMutableStateFlow.value.address != input) {
            viewModelScope.launch { updateOnAddressClickedUseCase.invoke(false) }
            formMutableStateFlow.update {
                it.copy(isAddressValid = false)
            }
        }
        viewModelScope.launch { setSelectedAddressStateUseCase.invoke(input) }
        formMutableStateFlow.update {
            it.copy(address = input?.ifBlank { null })
        }
    }

    fun onAddressEditTextFocused(hasFocus: Boolean) {
        setHasAddressFocusUseCase.invoke(hasFocus)
    }

    fun onPriceChanged(price: String?) {
        formMutableStateFlow.update { form ->
            form.copy(price = if (price.isNullOrBlank()) BigDecimal.ZERO else BigDecimal(price))
        }
    }

    fun onSurfaceChanged(surface: String?) {
        formMutableStateFlow.update { form ->
            form.copy(surface = if (surface.isNullOrBlank()) BigDecimal.ZERO else BigDecimal(surface))
        }
    }

    fun onRoomsNumberChanged(rooms: String?) {
        formMutableStateFlow.update { form ->
            form.copy(rooms = if (rooms.isNullOrBlank()) 0 else rooms.toInt())
        }
    }

    fun onBedroomsNumberChanged(bedrooms: String?) {
        formMutableStateFlow.update { form ->
            form.copy(bedrooms = if (bedrooms.isNullOrBlank()) 0 else bedrooms.toInt())
        }
    }

    fun onBathroomsNumberChanged(bathrooms: String?) {
        formMutableStateFlow.update { form ->
            form.copy(bathrooms = if (bathrooms.isNullOrBlank()) 0 else bathrooms.toInt())
        }
    }

    fun onSoldStatusChanged(checked: Boolean) {
        formMutableStateFlow.update {
            it.copy(
                isSold = checked,
                soldDate = if (!checked) null else LocalDate.now()
                )
        }
    }

    fun onAddPropertyClicked() {
        formMutableStateFlow.update {
            it.copy(
                price = convertToUsdDependingOnLocaleUseCase.invoke(formMutableStateFlow.value.price),
                surface = convertToSquareFeetDependingOnLocaleUseCase.invoke(formMutableStateFlow.value.surface)
            )
        }
        onSubmitButtonClickedMutableSharedFlow.tryEmit(Unit)
    }

    suspend fun getCurrentNavigationFragment(): NavigationFragmentType = getNavigationTypeUseCase.invoke().first()

    fun setNavFragment() = setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
}