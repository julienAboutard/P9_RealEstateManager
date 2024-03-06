package com.example.realestatemanager.domain.navigation

import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.data.navigation.NavigationRepository
import javax.inject.Inject

class SetNavigationTypeUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository,
) {
    fun invoke(navigationFragmentType: NavigationFragmentType) {
        navigationRepository.setNavigationFragmentType(navigationFragmentType)
    }
}