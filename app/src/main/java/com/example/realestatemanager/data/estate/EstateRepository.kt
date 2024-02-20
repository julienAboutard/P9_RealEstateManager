package com.example.realestatemanager.data.estate

import com.example.realestatemanager.data.estate.entity.EstateEntity
import kotlinx.coroutines.flow.Flow

interface EstateRepository {

    suspend fun add(estateEntity: EstateEntity): Long?
    suspend fun addEstateWithDetails(estateEntity: EstateEntity): Boolean
    fun getEstatesAsFlow(): Flow<List<EstateEntity>>
    fun getEstatesCountAsFlow(): Flow<Int>
    fun getEstateByIdAsFlow(estateId: Long): Flow<EstateEntity?>
    suspend fun getEstateById(estateId: Long): EstateEntity
    suspend fun update(estateEntity: EstateEntity): Boolean
}