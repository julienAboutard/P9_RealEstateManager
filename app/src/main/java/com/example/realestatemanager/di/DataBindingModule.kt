package com.example.realestatemanager.di

import com.emplk.realestatemanager.data.autocomplete.PredictionRepositoryAutocomplete
import com.emplk.realestatemanager.domain.geocoding.GeocodingRepository
import com.example.realestatemanager.data.agent.RealEstateAgentRepository
import com.example.realestatemanager.data.agent.RealEstateAgentRepositoryImpl
import com.example.realestatemanager.data.amenity.AmenityTypeRepository
import com.example.realestatemanager.data.amenity.AmenityTypeRepositoryImpl
import com.example.realestatemanager.data.autocomplete.PredictionRepository
import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepository
import com.example.realestatemanager.data.autocomplete.address.PredictionAddressStateRepositoryImpl
import com.example.realestatemanager.data.connectivity.GpsConnectivityRepository
import com.example.realestatemanager.data.connectivity.GpsConnectivityRepositoryBroadcastReceiver
import com.example.realestatemanager.data.connectivity.InternetConnectivityRepository
import com.example.realestatemanager.data.connectivity.InternetConnectivityRepositoryBroadcastReceiver
import com.example.realestatemanager.data.content.resolver.MediaFileRepository
import com.example.realestatemanager.data.content.resolver.MediaFileRepositoryContentResolver
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.data.estate.EstateRepositoryRoom
import com.example.realestatemanager.data.estate.current.CurrentEstateRepository
import com.example.realestatemanager.data.estate.current.CurrentEstateRepositoryImpl
import com.example.realestatemanager.data.estate.type.EstateTypeRepository
import com.example.realestatemanager.data.estate.type.EstateTypeRepositoryImpl
import com.example.realestatemanager.data.filter.EstatesFilterRepository
import com.example.realestatemanager.data.filter.EstatesFilterRepositoryImpl
import com.example.realestatemanager.data.formatting.FormattingRepository
import com.example.realestatemanager.data.formatting.FormattingRepositoryImpl
import com.example.realestatemanager.data.geocoding.GeocodingRepositoryGoogle
import com.example.realestatemanager.data.geolocation.GeolocationRepository
import com.example.realestatemanager.data.geolocation.GeolocationRepositoryFusedLocationProvider
import com.example.realestatemanager.data.media.MediaRepository
import com.example.realestatemanager.data.media.MediaRepositoryRoom
import com.example.realestatemanager.data.navigation.NavigationRepository
import com.example.realestatemanager.data.navigation.NavigationRepositoryImpl
import com.example.realestatemanager.data.permission.PermissionRepository
import com.example.realestatemanager.data.permission.PermissionRepositoryImpl
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

    @Singleton
    @Binds
    abstract fun bindEstatesFilterRepository(implementation: EstatesFilterRepositoryImpl): EstatesFilterRepository

    @Singleton
    @Binds
    abstract fun bindFormattingRepository(implementation: FormattingRepositoryImpl): FormattingRepository

    @Singleton
    @Binds
    abstract fun bindNavigationRepository(implementation: NavigationRepositoryImpl): NavigationRepository

    @Singleton
    @Binds
    abstract fun bindInternetConnectivityRepository(implementation: InternetConnectivityRepositoryBroadcastReceiver): InternetConnectivityRepository

    @Singleton
    @Binds
    abstract fun bingGpsConnectivityRepository(implementation: GpsConnectivityRepositoryBroadcastReceiver): GpsConnectivityRepository

    @Singleton
    @Binds
    abstract fun bindMediaFileRepository(implementation: MediaFileRepositoryContentResolver): MediaFileRepository

    @Singleton
    @Binds
    abstract fun bindGeolocationRepository(implementation: GeolocationRepositoryFusedLocationProvider): GeolocationRepository

    @Singleton
    @Binds
    abstract fun bindPermissionRepository(implementation: PermissionRepositoryImpl): PermissionRepository

    @Singleton
    @Binds
    abstract fun bindPredictionRepository(implementation: PredictionRepositoryAutocomplete): PredictionRepository

    @Singleton
    @Binds
    abstract fun bindGeoCodingRepository(implementation: GeocodingRepositoryGoogle): GeocodingRepository

    @Singleton
    @Binds
    abstract fun bindSelectedAddressStateRepository(implementation: PredictionAddressStateRepositoryImpl): PredictionAddressStateRepository
}