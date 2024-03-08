package com.example.realestatemanager.ui.map

import com.example.realestatemanager.ui.utils.EquatableCallbackWithParam
import com.google.android.gms.maps.model.LatLng

data class MarkerViewState(
    val userCurrentLocation: LatLng?,
    val fallbackLocationGoogleHq: LatLng,
    val estateMarkers: List<PropertyMarkerViewState>,
)

data class PropertyMarkerViewState(
    val propertyId: Long,
    val latLng: LatLng,
    val isSold: Boolean,
    val onMarkerClicked: EquatableCallbackWithParam<Long>,
)
