package com.example.realestatemanager.domain.estate

import com.emplk.realestatemanager.domain.geocoding.GeocodingRepository
import com.example.realestatemanager.R
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.data.estate.entity.EstateEntity
import com.example.realestatemanager.data.geocoding.GeocodingWrapper
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.domain.connectivity.IsInternetEnabledFlowUseCase
import com.example.realestatemanager.domain.estate.current.ResetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.form.FormParams
import com.example.realestatemanager.domain.media.DeleteMediaUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.ui.estate.add.AddEvent
import com.example.realestatemanager.ui.utils.NativeText
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class AddEstateUseCase @Inject constructor(
    private val estateRepository: EstateRepository,
    private val geocodingRepository: GeocodingRepository,
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val clock: Clock,
    private val resetCurrentEstateIdUseCase: ResetCurrentEstateIdUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
) {

    suspend fun invoke(form: FormParams): AddEvent {

        val addOrEditEstateWrapper: AddOrEditEstateWrapper = coroutineScope {

            val geocodingWrapperDeferred = async {
                geocodingRepository.getLatLong(form.address ?:"")
            }
            if (form.addOrEditStatus == "edit") {
                // region edit existing property

                //setPropertyInsertingInDatabaseUseCase.invoke(true)
                for (media in form.mediasInit) {
                    deleteMediaUseCase.invoke(media.id)
                }

                val updateSuccess = estateRepository.update(
                    EstateEntity(
                        id = form.id,
                        type = form.type ?: "ALL",
                        price = form.price,
                        surface = form.surface,
                        description = form.description ?: "",
                        rooms = form.rooms,
                        bathrooms = form.bathrooms,
                        location = form.address ?: "",
                        bedrooms = form.bedrooms,
                        latitude = getLatitude(geocodingWrapperDeferred.await()),
                        longitude = getLongitude(geocodingWrapperDeferred.await()),
                        agentName = form.agent ?: "To Define",
                        amenities = form.selectedAmenities,
                        medias = form.medias,
                        entryDate = form.entryDate!!,
                        saleDate = form.soldDate,
                    ),
                )
                resetCurrentEstateIdUseCase.invoke()

                if (updateSuccess) {
                    //setPropertyInsertingInDatabaseUseCase.invoke(false)
                    AddOrEditEstateWrapper.Success(NativeText.Resource(R.string.form_successfully_updated_message))
                } else {
                    //setPropertyInsertingInDatabaseUseCase.invoke(false)
                    AddOrEditEstateWrapper.Error(NativeText.Resource(R.string.form_generic_error_message))
                }
                // endregion
            } else {
                // region add new property
                val now = LocalDate.now(clock)
                //setPropertyInsertingInDatabaseUseCase.invoke(true)
                val addSuccess = estateRepository.addEstateWithDetails(
                    EstateEntity(
                        id = 0L,
                        type = form.type ?: "ALL",
                        price = form.price,
                        surface = form.surface,
                        description = form.description ?: "",
                        rooms = form.rooms,
                        bathrooms = form.bathrooms,
                        location = form.address ?: "",
                        bedrooms = form.bedrooms,
                        latitude = getLatitude(geocodingWrapperDeferred.await()),
                        longitude = getLongitude(geocodingWrapperDeferred.await()),
                        agentName = form.agent ?: "Shiro Almira",
                        amenities = form.selectedAmenities,
                        medias = form.medias,
                        entryDate = now,
                        saleDate = null,
                    ),
                )

                if (addSuccess) {
                    //setPropertyInsertingInDatabaseUseCase.invoke(false)
                    AddOrEditEstateWrapper.Success(NativeText.Resource(R.string.form_successfully_created_message))
                } else {
                    //setPropertyInsertingInDatabaseUseCase.invoke(false)
                    AddOrEditEstateWrapper.Error(NativeText.Resource(R.string.form_generic_error_message))
                }
            }
        }
        // endregion
        return when (addOrEditEstateWrapper) {
            is AddOrEditEstateWrapper.Success -> {
                setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
                AddEvent.Toast(addOrEditEstateWrapper.text)
            }

            is AddOrEditEstateWrapper.Error -> {
                setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
                AddEvent.Toast(addOrEditEstateWrapper.error)
            }

            is AddOrEditEstateWrapper.NoInternet -> {
                setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
                AddEvent.Toast(addOrEditEstateWrapper.error)
            }

            is AddOrEditEstateWrapper.NoLatLong -> {
                AddEvent.Toast(addOrEditEstateWrapper.error)
            }

            is AddOrEditEstateWrapper.LocaleError -> AddEvent.Toast(addOrEditEstateWrapper.error)
        }
    }

    private fun getLatitude(
        geocodingWrapper: GeocodingWrapper
    ): Double? {
        return when (geocodingWrapper) {
                is GeocodingWrapper.Success -> geocodingWrapper.latLng.latitude
                is GeocodingWrapper.Error,
                is GeocodingWrapper.NoResult -> null
            }
    }

    private fun getLongitude(
        geocodingWrapper: GeocodingWrapper
    ): Double? {
        return when (geocodingWrapper) {
            is GeocodingWrapper.Success -> geocodingWrapper.latLng.longitude
            is GeocodingWrapper.Error,
            is GeocodingWrapper.NoResult -> null

        }
    }
}