package com.example.realestatemanager.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.realestatemanager.data.agent.room.AgentDao
import com.example.realestatemanager.data.agent.room.AgentDto
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.estate.room.EstateDto
import com.example.realestatemanager.data.media.room.MediaDao
import com.example.realestatemanager.data.media.room.MediaDto
import com.example.realestatemanager.data.utils.BigDecimalTypeConverter
import com.example.realestatemanager.data.utils.LocalDateTimeTypeConverter


@Database(
    entities = [
        EstateDto::class,
        MediaDto::class,
        AgentDto::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeTypeConverter::class,
    BigDecimalTypeConverter::class
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getEstateDao(): EstateDao
    abstract fun getMediaDao(): MediaDao
    abstract fun getAgentDao(): AgentDao

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