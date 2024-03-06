package com.example.realestatemanager.data.navigation

import kotlinx.coroutines.flow.Flow

interface NavigationRepository {
    fun getNavigationFragmentType(): Flow<NavigationFragmentType>
    fun setNavigationFragmentType(navigationFragmentType: NavigationFragmentType)
}