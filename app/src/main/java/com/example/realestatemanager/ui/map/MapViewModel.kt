package com.example.realestatemanager.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.realestatemanager.data.geolocation.GeolocationState
import com.example.realestatemanager.domain.estate.GetEstatesAsFlowUseCase
import com.example.realestatemanager.domain.geolocation.GetCurrentLocationUseCase
import com.example.realestatemanager.domain.estate.current.SetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.permission.SetLocationPermissionUseCase
import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.example.realestatemanager.ui.utils.Event
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getEstatesAsFlowUseCase: GetEstatesAsFlowUseCase,
    private val setCurrentEstateIdUseCase: SetCurrentEstateIdUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val setLocationPermissionUseCase: SetLocationPermissionUseCase,
) : ViewModel() {

    companion object {
        /**
         * Default is Upper East Side, could be changed to user's last known location or any other chosen location
         */
        private val FALLBACK_LOCATION = LatLng(40.7736, -73.9566)
    }

    private val eventMutableSharedFlow: MutableSharedFlow<MapEvent> = MutableSharedFlow(extraBufferCapacity = 1)

    val viewState: LiveData<MarkerViewState> = liveData {
        if (latestValue == null) {
            emit(
                MarkerViewState(
                    userCurrentLocation = null,
                    fallbackLocationGoogleHq = FALLBACK_LOCATION,
                    estateMarkers = emptyList()
                )
            )
        }

        combine(
            getEstatesAsFlowUseCase.invoke(),
            getCurrentLocationUseCase.invoke()
        ) { propertiesLatLong, geolocationState ->
            MarkerViewState(
                userCurrentLocation = when (geolocationState) {
                    is GeolocationState.Success -> LatLng(
                        geolocationState.latitude,
                        geolocationState.longitude
                    )

                    is GeolocationState.Error -> {
                        eventMutableSharedFlow.tryEmit(MapEvent.Toast(geolocationState.message))
                        null
                    }

                },
                fallbackLocationGoogleHq = FALLBACK_LOCATION,
                estateMarkers = if (propertiesLatLong.isEmpty()) emptyList() else
                    propertiesLatLong.filter { it.latitude != null && it.longitude !=null }.map { propertyLatLong ->
                        PropertyMarkerViewState(
                            propertyId = propertyLatLong.id,
                            latLng = LatLng(
                                propertyLatLong.latitude!!,
                                propertyLatLong.longitude!!
                            ),
                            isSold = propertyLatLong.saleDate != null,
                            onMarkerClicked = EquatableCallbackWithParam { propertyId ->
                                setCurrentEstateIdUseCase.invoke(propertyId)
                                eventMutableSharedFlow.tryEmit(MapEvent.OnMarkerClicked)
                            },
                        )
                    }
            )
        }.collectLatest { emit(it) }
    }

    val viewEvent: LiveData<Event<MapEvent>> = liveData {
        eventMutableSharedFlow.collect {
            emit(Event(it))
        }
    }

    fun hasPermissionBeenGranted(isPermissionGranted: Boolean?) {
        setLocationPermissionUseCase.invoke(isPermissionGranted ?: false)
    }
}
