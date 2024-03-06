package com.example.realestatemanager.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.estate.room.EstateDto
import com.example.realestatemanager.data.media.room.MediaDao
import com.example.realestatemanager.data.media.room.MediaDto
import com.example.realestatemanager.data.utils.BigDecimalTypeConverter
import com.example.realestatemanager.data.utils.LocalDateTypeConverter


@Database(
    entities = [
        EstateDto::class,
        MediaDto::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateTypeConverter::class,
    BigDecimalTypeConverter::class
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getEstateDao(): EstateDao
    abstract fun getMediaDao(): MediaDao

    companion object {
        private const val DATABASE_NAME = "RealEstateManager_database"

        fun create(
            application: Application,
        ): AppDatabase =
            Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .build()
    }
}