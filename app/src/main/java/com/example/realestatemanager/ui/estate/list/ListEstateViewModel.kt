package com.example.realestatemanager.ui.estate.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.realestatemanager.R
import com.example.realestatemanager.data.estate.entity.EstateEntity
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.estate.GetEstatesAsFlowUseCase
import com.example.realestatemanager.domain.estate.current.ResetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.estate.current.SetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.estate.type.GetStringResourceForEstateTypeUseCase
import com.example.realestatemanager.domain.filter.GetEstatesFilterFlowUseCase
import com.example.realestatemanager.domain.filter.IsEstateMatchingFiltersUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ListEstateViewModel @Inject constructor(
    private val getEstatesAsFlowUseCase: GetEstatesAsFlowUseCase,
    private val setCurrentEstateIdUseCase: SetCurrentEstateIdUseCase,
    private val getStringResourceForEstateTypeUseCase: GetStringResourceForEstateTypeUseCase,
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase,
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase,
    private val formatAndRoundSurfaceToHumanReadableUseCase: FormatAndRoundSurfaceToHumanReadableUseCase,
    private val formatPriceToHumanReadableUseCase: FormatPriceToHumanReadableUseCase,
    private val getEstatesFilterFlowUseCase: GetEstatesFilterFlowUseCase,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val isEstateMatchingFiltersUseCase: IsEstateMatchingFiltersUseCase,
    private val resetCurrentEstateIdUseCase: ResetCurrentEstateIdUseCase,
) : ViewModel() {

    val viewState: LiveData<List<ListEstateViewState>> = liveData {
        /*if (latestValue == null) {
            emit(
                listOf(ListEstateViewState.LoadingStateEstate)
            )
        }*/

        combine(
            getEstatesAsFlowUseCase.invoke(),
            getEstatesFilterFlowUseCase.invoke()
        ) { estates: List<EstateEntity>, estatesFilter ->
            /*if (estates.isEmpty()) {
                emit(
                    listOf(
                        ListEstateViewState.EmptyState(
                            onAddClick = EquatableCallback {
                                setNavigationTypeUseCase.invoke(NavigationFragmentType.ADD_FRAGMENT)
                            }
                        )
                    )
                )
            } else {*/
                val mapEstates = estates.map { estate ->
                    val convertedPrice = convertToEuroDependingOnLocaleUseCase.invoke(estate.price)
                    val convertedSurface = convertToSquareMeterDependingOnLocaleUseCase.invoke(estate.surface)
                    estate.copy(
                        price = convertedPrice,
                        surface = convertedSurface
                    )
                }

                emit(
                    mapEstates
                        .asSequence()
                        .filter { estate ->
                            isEstateMatchingFiltersUseCase.invoke(
                                estate.type,
                                estate.location,
                                estate.price,
                                estate.surface,
                                estate.medias.count(),
                                estate.amenities,
                                estate.entryDate,
                                estate.saleDate,
                                estate.saleDate != null,
                                estatesFilter
                            )
                        }
                        .sortedBy { it.saleDate != null }
                        .map { estate ->
                            val pictureURl = estate.medias.find { it.isFeatured }?.uri
                            val featuredPicture = pictureURl?.let { NativePhoto.Uri(it) }
                                ?: NativePhoto.Resource(R.drawable.baseline_add_home_24)
                            ListEstateViewState(
                                id = estate.id,
                                estateType = getStringResourceForEstateTypeUseCase.invoke(estate.type),
                                featuredPicture = featuredPicture,
                                location = estate.location,
                                price = formatPriceToHumanReadableUseCase.invoke(estate.price),
                                isSold = estate.saleDate != null,
                                surface = formatAndRoundSurfaceToHumanReadableUseCase.invoke(estate.surface),
                                room = NativeText.Argument(R.string.detail_number_of_room_textview, estate.rooms),
                                bedroom = NativeText.Argument(R.string.detail_number_of_bedroom_textview, estate.bedrooms),
                                bathroom = NativeText.Argument(R.string.detail_number_of_bathroom_textview, estate.bathrooms),
                                entryDate = estate.entryDate,
                                amenities = estate.amenities,
                                onClickEvent = EquatableCallback {
                                    setCurrentEstateIdUseCase.invoke(estate.id)
                                }
                            )
                        }
                        .toList()
                )
           //}
        }.collect()
    }
    fun onObjectClicked(listEstateViewState: ListEstateViewState) {
        setCurrentEstateIdUseCase.invoke(listEstateViewState.id)
    }
    fun onAddEstateClicked() {
        setNavigationTypeUseCase.invoke(NavigationFragmentType.ADD_FRAGMENT)
        resetCurrentEstateIdUseCase.invoke()
    }
}