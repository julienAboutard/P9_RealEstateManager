package com.example.realestatemanager.ui.estate.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.realestatemanager.R
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateUseCase
import com.example.realestatemanager.domain.estate.type.GetStringResourceForEstateTypeUseCase
import com.example.realestatemanager.domain.formatting.ConvertToSquareMeterDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.ConvertToEuroDependingOnLocaleUseCase
import com.example.realestatemanager.domain.formatting.FormatAndRoundSurfaceToHumanReadableUseCase
import com.example.realestatemanager.domain.formatting.FormatPriceToHumanReadableUseCase
import com.example.realestatemanager.domain.map_picture.GenerateMapBaseUrlWithParamsUseCase
import com.example.realestatemanager.domain.map_picture.GenerateMapUrlWithApiKeyUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.detail.medialist.MediaViewState
import com.example.realestatemanager.ui.utils.NativePhoto
import com.example.realestatemanager.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val formatPriceToHumanReadableUseCase: FormatPriceToHumanReadableUseCase,
    private val convertToEuroDependingOnLocaleUseCase: ConvertToEuroDependingOnLocaleUseCase,
    private val convertToSquareMeterDependingOnLocaleUseCase: ConvertToSquareMeterDependingOnLocaleUseCase,
    private val formatAndRoundSurfaceToHumanReadableUseCase: FormatAndRoundSurfaceToHumanReadableUseCase,
    private val getStringResourceForEstateTypeUseCase: GetStringResourceForEstateTypeUseCase,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val generateMapBaseUrlWithParamsUseCase: GenerateMapBaseUrlWithParamsUseCase,
    private val generateMapUrlWithApiKeyUseCase: GenerateMapUrlWithApiKeyUseCase,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState: LiveData<DetailViewState> = liveData {

        getCurrentEstateUseCase.invoke().collect() { estate ->
            emit(
                DetailViewState(
                    id = estate.id,
                    estateType = getStringResourceForEstateTypeUseCase.invoke(estate.type),
                    medias = estate.medias
                        .sortedByDescending { it.isFeatured }
                        .map { media ->
                            MediaViewState(
                                mediaUri = NativePhoto.Uri(media.uri),
                                description = media.description,
                                type = media.type
                            )
                        },
                    mapMiniature = if (estate.latitude != null && estate.longitude != null) NativePhoto.Uri(
                        generateMapUrlWithApiKeyUseCase.invoke(
                            generateMapBaseUrlWithParamsUseCase.invoke(estate.latitude, estate.longitude)
                        )
                    ) else NativePhoto.Resource(
                        R.drawable.baseline_map_24
                    ),
                    price = formatPriceToHumanReadableUseCase.invoke(
                        convertToEuroDependingOnLocaleUseCase.invoke(estate.price)
                    ),
                    surface = formatAndRoundSurfaceToHumanReadableUseCase.invoke(
                        convertToSquareMeterDependingOnLocaleUseCase.invoke(estate.surface)
                    ),
                    rooms = NativeText.Argument(
                        R.string.detail_number_of_room_textview,
                        estate.rooms
                    ),
                    bathrooms = NativeText.Argument(
                        R.string.detail_number_of_bathroom_textview,
                        estate.bathrooms
                    ),
                    bedrooms = NativeText.Argument(
                        R.string.detail_number_of_bedroom_textview,
                        estate.bedrooms
                    ),
                    description = estate.description,
                    address = NativeText.Argument(
                        R.string.detail_location_tv,
                        estate.location
                    ),
                    amenities = estate.amenities.map { amenity ->
                        AmenityViewState.AmenityItem(
                            stringRes = amenity.stringRes,
                            iconDrawable = amenity.iconDrawable,
                        )
                    },
                    entryDate = NativeText.Argument(
                        R.string.detail_entry_date_tv,
                        estate.entryDate.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.SHORT
                            )
                        )
                    ),
                    agentName = NativeText.Argument(
                        R.string.detail_manager_agent_name,
                        estate.agentName
                    ),
                    isSold = estate.saleDate != null,
                    saleDate = estate.saleDate?.let { saleDate ->
                        NativeText.Argument(
                            R.string.detail_sold_date_tv,
                            saleDate.format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.SHORT
                                )
                            )
                        )
                    }
                )
            )
        }
    }

    fun onEditClicked() {
        setNavigationTypeUseCase.invoke(NavigationFragmentType.EDIT_FRAGMENT)
    }
}