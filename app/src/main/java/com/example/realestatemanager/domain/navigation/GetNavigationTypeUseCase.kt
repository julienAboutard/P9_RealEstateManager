package com.example.realestatemanager.domain.navigation

import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.data.navigation.NavigationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNavigationTypeUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository,
) {
    fun invoke(): Flow<NavigationFragmentType> = navigationRepository.getNavigationFragmentType()
}