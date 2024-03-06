package com.example.realestatemanager.ui.filter

import androidx.annotation.StringRes
import com.example.realestatemanager.ui.estate.add.amenity.AmenityViewState
import com.example.realestatemanager.ui.estate.add.type.TypeViewStateItem
import com.example.realestatemanager.ui.utils.EquatableCallback
import com.example.realestatemanager.ui.utils.NativeText
import java.time.LocalDate

data class FilterViewState(
    @StringRes val estateType: Int?,
    val address: String,
    val minPrice: String,
    val maxPrice: String,
    val minSurface: String,
    val maxSurface: String,
    val minMedia: String,
    val amenities: List<AmenityViewState>,
    val estateTypes: List<TypeViewStateItem>,
    val researchDate: LocalDate?,
    val availableForSale: EstateSaleState? = EstateSaleState.ALL,
    val isFilterButtonEnabled: Boolean,
    val onFilterClicked: EquatableCallback,
    val onCancelClicked: EquatableCallback,
)