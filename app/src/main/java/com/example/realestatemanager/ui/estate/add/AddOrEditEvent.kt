package com.example.realestatemanager.ui.estate.add

import com.example.realestatemanager.ui.utils.NativeText

sealed class AddOrEditEvent {
    object Loading : AddOrEditEvent()
    object Form : AddOrEditEvent()
    data class Toast(val text: NativeText) : AddOrEditEvent()
    data class Error(val errorMessage: String) : AddOrEditEvent()
}
