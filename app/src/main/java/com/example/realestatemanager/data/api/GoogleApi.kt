package com.example.realestatemanager.data.api

import com.example.realestatemanager.data.autocomplete.response.AutocompleteResponse
import com.example.realestatemanager.data.geocoding.response.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {

    @GET("place/autocomplete/json")
    suspend fun getAddressPredictions(
        @Query("input") input: String,
        @Query("type") type: String
    ): AutocompleteResponse

    @GET("geocode/json")
    suspend fun getGeocode(
        @Query("address") address: String,
    ): GeocodingResponse
}