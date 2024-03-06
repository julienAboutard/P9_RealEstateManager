package com.example.realestatemanager.di

import android.app.Application
import android.content.ContentResolver
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.LruCache
import androidx.core.content.getSystemService
import com.example.realestatemanager.BuildConfig
import com.example.realestatemanager.data.api.GoogleApi
import com.example.realestatemanager.data.autocomplete.PredictionWrapper
import com.example.realestatemanager.data.database.AppDatabase
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.geocoding.GeocodingWrapper
import com.example.realestatemanager.data.media.room.MediaDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Clock
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideAppDatabase(
        application: Application,
    ): AppDatabase = AppDatabase.create(application)

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Singleton
    @Provides
    fun provideGoogleApiService(@GoogleApiRetrofit retrofit: Retrofit): GoogleApi =
        retrofit.create(GoogleApi::class.java)

    @Singleton
    @Provides
    @GoogleApiRetrofit
    fun provideGoogleApiRetrofit(@GoogleApiOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Singleton
    @Provides
    @GoogleApiOkHttpClient
    fun provideGoogleOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    chain.proceed(
                        chain.request().let { request ->
                            request
                                .newBuilder()
                                .url(
                                    request.url.newBuilder()
                                        .addQueryParameter("key", BuildConfig.GOOGLE_MAPS_API_KEY)
                                        .build()
                                )
                                .build()
                        }
                    )
                }
            )
            .build()

    @Suppress("DEPRECATION")
    @Singleton
    @Provides
    fun provideLocale(application: Application): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            application.resources.configuration.locales[0]
        } else {
            application.resources.configuration.locale
        }
    }

    @Singleton
    @Provides
    fun provideContentResolver(application: Application): ContentResolver = application.contentResolver

    // region Dao

    @Singleton
    @Provides
    fun provideEstateDao(appDatabase: AppDatabase): EstateDao = appDatabase.getEstateDao()

    @Singleton
    @Provides
    fun provideMediaDao(appDatabase: AppDatabase): MediaDao = appDatabase.getMediaDao()

    // endregion Dao

    @Singleton
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Singleton
    @Provides
    fun provideConnectivityManager(application: Application): ConnectivityManager? = application.getSystemService()

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Singleton
    @Provides
    fun provideLocationManager(application: Application): LocationManager? = application.getSystemService()

    @Singleton
    @Provides
    @CurrentVersionCode
    fun provideCurrentVersion(): Int = Build.VERSION.SDK_INT

    @Singleton
    @Provides
    @LruCachePredictions
    fun providePredictionsLruCache(): LruCache<String, PredictionWrapper> = LruCache(200)

    @Singleton
    @Provides
    @LruCacheGeocode
    fun provideGeocodeLruCache(): LruCache<String, GeocodingWrapper> = LruCache(200)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CurrentVersionCode

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LruCachePredictions

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LruCacheGeocode

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleApiRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleApiOkHttpClient
}