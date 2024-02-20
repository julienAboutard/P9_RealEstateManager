package com.example.realestatemanager.data.estate.current

import kotlinx.coroutines.flow.Flow

interface CurrentEstateRepository {
    fun getCurrentEstateIdAsFlow(): Flow<Long?>
    fun setCurrentEstateId(id: Long)
    fun resetCurrentEstateId()
}