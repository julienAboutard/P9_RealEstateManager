package com.example.realestatemanager.data.filter

import kotlinx.coroutines.flow.Flow

interface EstatesFilterRepository {

    fun setEstatesFilter(estatesFilterEntity: EstatesFilterEntity)
    fun getEstatesFilter(): Flow<EstatesFilterEntity?>
    fun resetEstatesFilter()
    fun setIsFiltered(isFiltered: Boolean)
    fun getIsFiltered(): Flow<Boolean?>
}