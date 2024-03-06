package com.example.realestatemanager.domain.formatting


import com.example.realestatemanager.data.formatting.FormattingRepository
import com.example.realestatemanager.data.formatting.type.CurrencyType
import javax.inject.Inject

class GetCurrencyTypeUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
) {
    fun invoke(): CurrencyType = formattingRepository.getLocaleCurrencyFormatting()
}