package com.example.realestatemanager.domain.formatting

import com.example.realestatemanager.data.formatting.FormattingRepository
import java.util.Locale
import javax.inject.Inject

class GetLocaleUseCase @Inject constructor(
    private val formattingRepository: FormattingRepository,
) {
    fun invoke(): Locale = formattingRepository.getLocale()
}
