package com.example.realestatemanager.ui.map

import com.example.realestatemanager.ui.utils.NativeText

sealed class MapEvent {
    object OnMarkerClicked : MapEvent()
    data class Toast(val message: NativeText?) : MapEvent()
}