package com.example.realestatemanager.ui.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.realestatemanager.R
import com.example.realestatemanager.data.amenity.AmenityType
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.amenity.GetAmenityTypeUseCase
import com.example.realestatemanager.domain.estate.type.GetEstateTypeUseCase
import com.example.realestatemanager.domain.filter.OptimizeValuesForFilteringUseCase
import com.example.realestatemanager.domain.filter.SetEstatesFilterUseCase
import com.example.realestatemanager.domain.filter.SetIsFilteredUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareFeetDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToUsdDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.navigation.GetNavigationTypeUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.Event
import com.example.realestatemanager.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Address
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FilterPropertiesViewModel @Inject constructor(
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase,
    private val convertToUsdDependingOnLocaleUseCase: ConvertToUsdDependingOnLocaleUseCase,
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase,
    private val convertToSquareFeetDependingOnLocaleUseCase: ConvertToSquareFeetDependingOnLocaleUseCase,
    private val getEstateTypeUseCase: GetEstateTypeUseCase,
    private val getAmenityTypeUseCase: GetAmenityTypeUseCase,
    private val setEstatesFilterUseCase: SetEstatesFilterUseCase,
    private val optimizeValuesForFilteringUseCase: OptimizeValuesForFilteringUseCase,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val getNavigationTypeUseCase: GetNavigationTypeUseCase,
    private val setIsFilteredUseCase: SetIsFilteredUseCase,
) : ViewModel() {

    private val filterParamsMutableStateFlow = MutableStateFlow(FilterParams())
    private val onFilterClickMutableSharedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val viewState: LiveData<FilterViewState> = liveData {

        val estateTypes = getEstateTypeUseCase.invoke()
        val amenityTypes = getAmenityTypeUseCase.invoke()

        emit(
            FilterViewState(
                estateType = estateTypes.find { it.databaseName == filterParamsMutableStateFlow.value.estateType }?.stringRes,
                address = filterParamsMutableStateFlow.value.address ?: "",
                minPrice = if (filterParamsMutableStateFlow.value.minPrice == BigDecimal.ZERO) ""
                else convertToEuroDependingOnLocaleUseCase.invoke(filterParamsMutableStateFlow.value.minPrice).toString(),
                maxPrice = if (filterParamsMutableStateFlow.value.maxPrice == BigDecimal.ZERO) ""
                else convertToEuroDependingOnLocaleUseCase.invoke(filterParamsMutableStateFlow.value.maxPrice).toString(),
                minSurface = if (filterParamsMutableStateFlow.value.minSurface == BigDecimal.ZERO) ""
                else convertToSquareMeterDependingOnLocaleUseCase.invoke(filterParamsMutableStateFlow.value.minSurface).toString(),
                maxSurface = if (filterParamsMutableStateFlow.value.maxSurface == BigDecimal.ZERO) ""
                else convertToSquareMeterDependingOnLocaleUseCase.invoke(filterParamsMutableStateFlow.value.maxSurface).toString(),
                amenities = mapAmenityTypesToViewStates(amenityTypes),
                estateTypes = estateTypes.map { estateType ->
                    TypeViewStateItem(
                        id = estateType.id,
                        name = NativeText.Resource(estateType.stringRes),
                        databaseName = estateType.databaseName
                    )
                },
                minMedia = if (filterParamsMutableStateFlow.value.minMedia == 0) ""
                else filterParamsMutableStateFlow.value.minMedia.toString(),
                researchDate = filterParamsMutableStateFlow.value.researchDate,
                availableForSale = filterParamsMutableStateFlow.value.saleState,
                isFilterButtonEnabled = filterParamsMutableStateFlow.value.estateType != null ||
                filterParamsMutableStateFlow.value.minPrice != BigDecimal.ZERO ||
                filterParamsMutableStateFlow.value.maxPrice != BigDecimal.ZERO ||
                filterParamsMutableStateFlow.value.minSurface != BigDecimal.ZERO ||
                filterParamsMutableStateFlow.value.maxSurface != BigDecimal.ZERO ||
                filterParamsMutableStateFlow.value.selectedAmenities.isNotEmpty() ||
                filterParamsMutableStateFlow.value.researchDate != null ||
                filterParamsMutableStateFlow.value.saleState != null,
                onCancelClicked = EquatableCallback {
                    filterParamsMutableStateFlow.update { FilterParams() }
                    onFilterClickMutableSharedFlow.tryEmit(Unit)
                },
                onFilterClicked = EquatableCallback {
                    if (filterParamsMutableStateFlow.value != FilterParams()) {
                        viewModelScope.launch {
                            setEstatesFilterUseCase.invoke(
                                filterParamsMutableStateFlow.value.estateType,
                                filterParamsMutableStateFlow.value.address,
                                filterParamsMutableStateFlow.value.minPrice,
                                filterParamsMutableStateFlow.value.maxPrice,
                                filterParamsMutableStateFlow.value.minSurface,
                                filterParamsMutableStateFlow.value.maxSurface,
                                filterParamsMutableStateFlow.value.minMedia,
                                filterParamsMutableStateFlow.value.researchDate,
                                filterParamsMutableStateFlow.value.saleState,
                                filterParamsMutableStateFlow.value.selectedAmenities,
                            )
                        }
                    }
                    setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
                    setIsFilteredUseCase.invoke(true)
                    onFilterClickMutableSharedFlow.tryEmit(Unit)
                }
            )
        )
    }

    val viewEvent: LiveData<Event<Unit>> = liveData {
        onFilterClickMutableSharedFlow.collectLatest {
            emit(Event(Unit))
        }
    }

    private fun mapAmenityTypesToViewStates(amenityTypes: List<AmenityType>): List<AmenityViewState> =
        amenityTypes.map { amenityType ->
            AmenityViewState.AmenityCheckbox(
                id = amenityType.id,
                name = amenityType.name,
                iconDrawable = amenityType.iconDrawable,
                stringRes = amenityType.stringRes,
                isChecked = filterParamsMutableStateFlow.value.selectedAmenities.contains(amenityType),
                onCheckBoxClicked = EquatableCallbackWithParam { isChecked ->
                    if (filterParamsMutableStateFlow.value.selectedAmenities.contains(amenityType) && !isChecked) {
                        filterParamsMutableStateFlow.update {
                            it.copy(selectedAmenities = it.selectedAmenities - amenityType)
                        }
                    } else if (!filterParamsMutableStateFlow.value.selectedAmenities.contains(amenityType) && isChecked) {
                        filterParamsMutableStateFlow.update {
                            it.copy(selectedAmenities = it.selectedAmenities + amenityType)
                        }
                    }
                },
            )
        }

    fun onPropertyTypeSelected(propertyType: String) {
        filterParamsMutableStateFlow.update {
            it.copy(estateType = propertyType)
        }
    }

    fun onMediaMinChanged(minMedia: String?) {
        if (minMedia.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(minMedia = 0)
            }
        } else {
            filterParamsMutableStateFlow.update {
                it.copy(minMedia = minMedia.toInt())
            }
        }
    }

    fun onEntryDateRangeStatusChanged(entryDate: LocalDate) {
        filterParamsMutableStateFlow.update {
            it.copy(researchDate = entryDate)
        }
    }

    fun onPropertySaleStateChanged(estateSaleState: EstateSaleState) {
        filterParamsMutableStateFlow.update {
            it.copy(saleState = estateSaleState)
        }
    }

    fun onResetFilters() {
        filterParamsMutableStateFlow.tryEmit(FilterParams())
    }

    fun onAddressChanged(address: String?) {
        Log.d("controle", "onAddressChanged: $address")
        if (address.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(address = "")
            }
        } else
            filterParamsMutableStateFlow.update {
                it.copy(address = address.trim())
            }
    }

    fun onMinPriceChanged(minPrice: String?) {
        if (minPrice.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(minPrice = BigDecimal.ZERO)
            }
        } else
            filterParamsMutableStateFlow.update {
                it.copy(minPrice = convertToUsdDependingOnLocaleUseCase.invoke(BigDecimal(minPrice)))
            }
    }

    fun onMaxPriceChanged(maxPrice: String?) {
        if (maxPrice.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(maxPrice = BigDecimal.ZERO)
            }
        } else
            filterParamsMutableStateFlow.update {
                it.copy(maxPrice = convertToUsdDependingOnLocaleUseCase.invoke(BigDecimal(maxPrice)))
            }
    }

    fun onMinSurfaceChanged(minSurface: String?) {
        if (minSurface.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(minSurface = BigDecimal.ZERO)
            }
        } else
            filterParamsMutableStateFlow.update {
                it.copy(minSurface = convertToSquareFeetDependingOnLocaleUseCase.invoke(BigDecimal(minSurface)))
            }
    }

    fun onMaxSurfaceChanged(maxSurface: String?) {
        if (maxSurface.isNullOrBlank()) {
            filterParamsMutableStateFlow.update {
                it.copy(maxSurface = BigDecimal.ZERO)
            }
        } else
            filterParamsMutableStateFlow.update {
                it.copy(maxSurface = convertToSquareFeetDependingOnLocaleUseCase.invoke(BigDecimal(maxSurface)))
            }
    }

    fun onStop() {
        setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
    }

    suspend fun getCurrentNavigationFragment(): NavigationFragmentType {
        val flowNavFrag = getNavigationTypeUseCase.invoke()
        return flowNavFrag.single()
    }
}

data class FilterParams(
    val estateType: String? = null,
    val address: String? = null,
    val minPrice: BigDecimal = BigDecimal.ZERO,
    val maxPrice: BigDecimal = BigDecimal.ZERO,
    val minSurface: BigDecimal = BigDecimal.ZERO,
    val maxSurface: BigDecimal = BigDecimal.ZERO,
    val minMedia: Int = 0,
    val selectedAmenities: List<AmenityType> = emptyList(),
    val saleState: EstateSaleState? = null,
    val researchDate: LocalDate? = null,
)