package com.example.realestatemanager.domain.connectivity

import com.example.realestatemanager.data.connectivity.InternetConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsInternetEnabledFlowUseCase @Inject constructor(
    private val internetConnectivityRepository: InternetConnectivityRepository,
) {
    fun invoke(): Flow<Boolean> = internetConnectivityRepository.isInternetEnabledAsFlow()
}