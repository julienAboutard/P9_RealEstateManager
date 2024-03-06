package com.example.realestatemanager.ui.map.bottom_sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.realestatemanager.ui.map.bottom_sheet.MapBottomSheetFragment.Companion.DETAIL_PROPERTY_TAG
import com.example.realestatemanager.ui.map.bottom_sheet.MapBottomSheetFragment.Companion.EDIT_PROPERTY_TAG
import com.example.realestatemanager.R
import com.example.realestatemanager.data.media.entity.MediaEntity
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.Event
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MapBottomSheetViewModel @Inject constructor(
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase,
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase,
    private val getRoundedSurfaceWithUnitHumanReadableUseCase: FormatAndRoundSurfaceToHumanReadableUseCase,
    private val formatPriceToHumanReadableUseCase: FormatPriceToHumanReadableUseCase,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
) : ViewModel() {

    private val onActionClickedMutableSharedFlow: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    private val isProgressBarVisibleMutableLiveData: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState: LiveData<PropertyMapBottomSheetViewState> = liveData {
        if (latestValue == null) isProgressBarVisibleMutableLiveData.tryEmit(true)
        getCurrentEstateUseCase.invoke().collectLatest { property ->
            isProgressBarVisibleMutableLiveData.tryEmit(false)

            val propertyWithConvertedPriceAndSurface = property.copy(
                price = convertToEuroDependingOnLocaleUseCase.invoke(property.price),
                surface = convertToSquareMeterDependingOnLocaleUseCase.invoke(property.surface)
            )

            emit(
                PropertyMapBottomSheetViewState(
                    propertyId = propertyWithConvertedPriceAndSurface.id,
                    type = propertyWithConvertedPriceAndSurface.type,
                    price = formatPriceToHumanReadableUseCase.invoke(propertyWithConvertedPriceAndSurface.price),
                    surface = getRoundedSurfaceWithUnitHumanReadableUseCase.invoke(propertyWithConvertedPriceAndSurface.surface),
                    featuredPicture = NativePhoto.Uri(
                        getFeaturedPictureUri(
                            propertyWithConvertedPriceAndSurface.medias
                        )
                    ),
                    isSold = propertyWithConvertedPriceAndSurface.saleDate != null,
                    onDetailClick = EquatableCallbackWithParam { fragmentTag ->
                        setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
                        onActionClickedMutableSharedFlow.tryEmit(fragmentTag)
                    },
                    onEditClick = EquatableCallbackWithParam { fragmentTag ->
                        setNavigationTypeUseCase.invoke(NavigationFragmentType.EDIT_FRAGMENT)
                        onActionClickedMutableSharedFlow.tryEmit(fragmentTag)
                    },
                    description = propertyWithConvertedPriceAndSurface.description,
                    rooms = NativeText.Argument(
                        R.string.rooms_nb_short_version,
                        propertyWithConvertedPriceAndSurface.rooms
                    ),
                    bedrooms = NativeText.Argument(
                        R.string.bedrooms_nb_short_version,
                        propertyWithConvertedPriceAndSurface.bedrooms
                    ),
                    bathrooms = NativeText.Argument(
                        R.string.bathrooms_nb_short_version,
                        propertyWithConvertedPriceAndSurface.bathrooms
                    ),
                    isProgressBarVisible = isProgressBarVisibleMutableLiveData.value,
                )
            )
        }
    }

    private fun getFeaturedPictureUri(medias: List<MediaEntity>): String = medias.first { it.isFeatured }.uri


    val viewEvent: LiveData<Event<MapBottomSheetEvent>> = liveData {
        onActionClickedMutableSharedFlow.collect {
            when (it) {
                EDIT_PROPERTY_TAG -> emit(Event(MapBottomSheetEvent.Edit))
                DETAIL_PROPERTY_TAG -> emit(Event(MapBottomSheetEvent.Detail))
            }
        }
    }
}

