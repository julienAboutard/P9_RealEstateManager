package com.example.realestatemanager.domain.formatting

import com.example.realestatemanager.data.formatting.FormattingRepository
import java.math.BigDecimal
import javax.inject.Inject

class FormatPriceToHumanReadableUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
) {
    fun invoke(price: BigDecimal): String = formattingRepository.formatRoundedPriceToHumanReadable(price)
}