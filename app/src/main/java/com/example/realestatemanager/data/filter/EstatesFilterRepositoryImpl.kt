package com.example.realestatemanager.data.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class EstatesFilterRepositoryImpl @Inject constructor(): EstatesFilterRepository {

    private val estatesFilterMutableStateFlow: MutableStateFlow<EstatesFilterEntity?> = MutableStateFlow(null)
    private val isFilteredMutableStateFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    override fun setEstatesFilter(estatesFilterEntity: EstatesFilterEntity) {
        estatesFilterMutableStateFlow.tryEmit(estatesFilterEntity)
    }

    override fun getEstatesFilter(): Flow<EstatesFilterEntity?> = estatesFilterMutableStateFlow

    override fun resetEstatesFilter() {
        estatesFilterMutableStateFlow.tryEmit(null)
    }

    override fun setIsFiltered(isFiltered: Boolean) {
        isFilteredMutableStateFlow.tryEmit(isFiltered)
    }

    override fun getIsFiltered(): Flow<Boolean?> = isFilteredMutableStateFlow
}