package com.example.realestatemanager.data.estate.current

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CurrentEstateRepositoryImpl @Inject constructor() : CurrentEstateRepository {

    private var currentEstateIdMutableSharedFlow: MutableStateFlow<Long?> = MutableStateFlow(null)

    override fun getCurrentEstateIdAsFlow(): Flow<Long?> = currentEstateIdMutableSharedFlow

    override fun setCurrentEstateId(id: Long) {
        currentEstateIdMutableSharedFlow.tryEmit(id)
    }

    override fun resetCurrentEstateId() {
        currentEstateIdMutableSharedFlow.tryEmit(null)
    }
}