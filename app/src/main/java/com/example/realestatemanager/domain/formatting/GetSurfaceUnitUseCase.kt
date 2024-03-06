package com.example.realestatemanager.domain.formatting

import com.example.realestatemanager.data.formatting.FormattingRepository
import com.example.realestatemanager.data.formatting.type.SurfaceUnitType
import javax.inject.Inject

class GetSurfaceUnitUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
) {
    fun invoke(): SurfaceUnitType = formattingRepository.getLocaleSurfaceUnitFormatting()
}