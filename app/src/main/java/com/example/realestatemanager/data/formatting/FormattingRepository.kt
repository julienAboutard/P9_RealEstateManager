package com.example.realestatemanager.data.formatting

import com.example.realestatemanager.data.formatting.type.CurrencyType
import com.example.realestatemanager.data.formatting.type.SurfaceUnitType
import java.math.BigDecimal
import java.util.Locale

interface FormattingRepository {

    fun getLocale(): Locale
    fun convertSquareFeetToSquareMetersRoundedHalfUp(squareFeet: BigDecimal): BigDecimal
    fun convertSquareMetersToSquareFeetRoundedHalfUp(squareMeters: BigDecimal): BigDecimal
    fun getLocaleSurfaceUnitFormatting(): SurfaceUnitType
    fun convertDollarToEuroRoundedHalfUp(dollar: BigDecimal,): BigDecimal
    fun convertEuroToDollarRoundedHalfUp(euro: BigDecimal): BigDecimal
    fun formatRoundedPriceToHumanReadable(price: BigDecimal): String
    fun getLocaleCurrencyFormatting(): CurrencyType
}