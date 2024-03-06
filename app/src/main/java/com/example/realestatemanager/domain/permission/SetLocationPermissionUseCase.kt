package com.example.realestatemanager.domain.permission

import com.example.realestatemanager.data.permission.PermissionRepository
import javax.inject.Inject

class SetLocationPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(permission: Boolean) = permissionRepository.setLocationPermission(permission)
}