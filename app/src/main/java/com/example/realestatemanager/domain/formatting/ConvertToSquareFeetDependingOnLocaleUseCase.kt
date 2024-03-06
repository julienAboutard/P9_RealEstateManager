package com.example.realestatemanager.domain.formatting

import com.example.realestatemanager.data.formatting.FormattingRepository
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

class ConvertToSquareFeetDependingOnLocaleUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
    private val getLocaleUseCase: GetLocaleUseCase,
) {
    fun invoke(surface: BigDecimal): BigDecimal =
        when (getLocaleUseCase.invoke()) {
            Locale.US -> surface
            Locale.FRANCE -> formattingRepository.convertSquareMetersToSquareFeetRoundedHalfUp(surface)
            else -> surface
        }
}