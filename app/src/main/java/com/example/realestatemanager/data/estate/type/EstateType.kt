package com.example.realestatemanager.data.estate.type

import androidx.annotation.StringRes
import com.example.realestatemanager.R

enum class EstateType (
    val id: Long,
    val databaseName: String,
    @StringRes val stringRes: Int
) {
    HOUSE(1L, "House", R.string.type_house),
    FLAT(2L, "Flat", R.string.type_flat),
    DUPLEX(3L, "Duplex", R.string.type_duplex),
    PENTHOUSE(4L, "Penthouse", R.string.type_penthouse),
    VILLA(5L, "Villa", R.string.type_villa),
    MANOR(6L, "Manor", R.string.type_manor),
    OTHER(7L, "Other", R.string.type_other),
    ALL(0L, "All", R.string.type_all)
}