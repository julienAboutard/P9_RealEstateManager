package com.example.realestatemanager.data.amenity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.realestatemanager.R

enum class AmenityType(
    val id: Long,
    @StringRes val stringRes: Int,
    @DrawableRes val iconDrawable: Int
) {
    SCHOOL(1, R.string.amenity_school, R.drawable.baseline_school_24),
    PARK(2, R.string.amenity_park, R.drawable.baseline_park_24),
    SHOPPING_MALL(3, R.string.amenity_shopping_mall, R.drawable.baseline_shopping_cart_24),
    RESTAURANT(4, R.string.amenity_restaurant, R.drawable.baseline_restaurant_24),
    PUBLIC_TRANSPORTATION(5, R.string.amenity_public_transportation, R.drawable.baseline_train_24),
    HOSPITAL(6, R.string.amenity_hospital, R.drawable.baseline_local_hospital_24),
}