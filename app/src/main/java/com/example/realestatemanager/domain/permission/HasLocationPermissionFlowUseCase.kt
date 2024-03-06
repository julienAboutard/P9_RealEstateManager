package com.example.realestatemanager.domain.permission

import com.example.realestatemanager.data.permission.PermissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasLocationPermissionFlowUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(): Flow<Boolean?> = permissionRepository.getLocationPermission()
}