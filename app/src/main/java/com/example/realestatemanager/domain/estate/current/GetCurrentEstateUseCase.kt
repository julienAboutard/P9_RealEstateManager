package com.example.realestatemanager.domain.estate.current

import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.data.estate.entity.EstateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetCurrentEstateUseCase @Inject constructor(
    private val estateRepository: EstateRepository,
    private val getCurrentEstateIdFlowUseCase: GetCurrentEstateIdFlowUseCase,
) {
    fun invoke(): Flow<EstateEntity> =
        getCurrentEstateIdFlowUseCase.invoke().filterNotNull().flatMapLatest { id ->
            estateRepository.getEstateByIdAsFlow(id)
        }.filterNotNull()
}