package com.example.realestatemanager.data.autocomplete

interface PredictionRepository {

    suspend fun getAddressPredictions(query: String): PredictionWrapper
}
