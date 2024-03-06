package com.example.realestatemanager.domain.formatting

import com.example.realestatemanager.data.formatting.FormattingRepository
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

class ConvertToEuroDependingOnLocaleUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
    private val getLocaleUseCase: GetLocaleUseCase,
) {
    fun invoke(price: BigDecimal): BigDecimal {
        if (price == BigDecimal.ZERO) return BigDecimal.ZERO
        return when (getLocaleUseCase.invoke()) {
            Locale.US -> price
            Locale.FRANCE -> formattingRepository.convertDollarToEuroRoundedHalfUp(price)
            else -> price
        }
    }
}
