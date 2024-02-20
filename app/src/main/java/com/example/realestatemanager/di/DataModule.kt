package com.example.realestatemanager.di

import com.example.realestatemanager.data.agent.room.AgentDao
import com.example.realestatemanager.data.database.AppDatabase
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.media.room.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    // region Dao

    @Singleton
    @Provides
    fun provideEstateDao(appDatabase: AppDatabase): EstateDao = appDatabase.getEstateDao()

    @Singleton
    @Provides
    fun provideMediaDao(appDatabase: AppDatabase): MediaDao = appDatabase.getMediaDao()

    @Singleton
    @Provides
    fun provideAgentDao(appDatabase: AppDatabase): AgentDao = appDatabase.getAgentDao()

    // endregion Dao
}