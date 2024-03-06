package com.example.realestatemanager.data.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateTypeConverter {

    @TypeConverter
    fun toDate(dateString: String?): LocalDate? = if (dateString == null) {
        null
    } else {
        LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? = date?.toString()
}