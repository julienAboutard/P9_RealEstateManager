package com.example.realestatemanager.ui.estate.add

import com.example.realestatemanager.ui.utils.NativeText

sealed class AddEvent {
    object Loading : AddEvent()
    object Form : AddEvent()
    data class Toast(val text: NativeText) : AddEvent()
    data class Error(val errorMessage: String) : AddEvent()
}
