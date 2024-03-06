package com.example.realestatemanager.ui.map.bottom_sheet

sealed class MapBottomSheetEvent {
    object Detail : MapBottomSheetEvent()
    object Edit : MapBottomSheetEvent()
}