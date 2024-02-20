package com.example.realestatemanager.di

import com.example.realestatemanager.data.agent.RealEstateAgentRepository
import com.example.realestatemanager.data.agent.RealEstateAgentRepositoryImpl
import com.example.realestatemanager.data.amenity.AmenityTypeRepository
import com.example.realestatemanager.data.amenity.AmenityTypeRepositoryImpl
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.data.estate.EstateRepositoryRoom
import com.example.realestatemanager.data.estate.current.CurrentEstateRepository
import com.example.realestatemanager.data.estate.current.CurrentEstateRepositoryImpl
import com.example.realestatemanager.data.estate.type.EstateTypeRepository
import com.example.realestatemanager.data.estate.type.EstateTypeRepositoryImpl
import com.example.realestatemanager.data.media.MediaRepository
import com.example.realestatemanager.data.media.MediaRepositoryRoom
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

    @Singleton
    @Binds
    abstract fun bindEstateRepository(implementation: EstateRepositoryRoom): EstateRepository

    @Singleton
    @Binds
    abstract fun bindMediaRepository(implementation: MediaRepositoryRoom): MediaRepository

    @Singleton
    @Binds
    abstract fun bindAgentRepository(implementation: RealEstateAgentRepositoryImpl): RealEstateAgentRepository

    @Singleton
    @Binds
    abstract fun bindEstateTypeRepository(implementation: EstateTypeRepositoryImpl): EstateTypeRepository

    @Singleton
    @Binds
    abstract fun bindAmenityTypeRepository(implementation: AmenityTypeRepositoryImpl): AmenityTypeRepository

    @Singleton
    @Binds
    abstract fun bindCurrentEstateRepository(implementation: CurrentEstateRepositoryImpl): CurrentEstateRepository
}