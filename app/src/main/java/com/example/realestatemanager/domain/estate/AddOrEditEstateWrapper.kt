package com.example.realestatemanager.domain.estate

import com.example.realestatemanager.ui.utils.NativeText

sealed class AddOrEditEstateWrapper {
    data class Success(val text: NativeText) : AddOrEditEstateWrapper()
    data class Error(val error: NativeText) : AddOrEditEstateWrapper()
    data class NoInternet(val error: NativeText) : AddOrEditEstateWrapper()
    data class NoLatLong(val error: NativeText) : AddOrEditEstateWrapper()
    data class LocaleError(val error: NativeText) : AddOrEditEstateWrapper()
}