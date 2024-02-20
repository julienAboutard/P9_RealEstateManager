package com.example.realestatemanager.data.utils

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalTypeConverter {

    @TypeConverter
    fun bigDecimalToDouble(bigDecimal: BigDecimal?): Double {
        return bigDecimal?.toDouble() ?: 0.0
    }

    @TypeConverter
    fun doubleToBigDecimal(value: Double?): BigDecimal {
        return value?.toBigDecimal() ?: BigDecimal.ZERO
    }
}