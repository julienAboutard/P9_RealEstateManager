package com.example.realestatemanager.data.formatting

import com.example.realestatemanager.data.formatting.type.CurrencyType
import com.example.realestatemanager.data.formatting.type.SurfaceUnitType
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class FormattingRepositoryImpl @Inject constructor(
    private val locale: Locale
): FormattingRepository {

    companion object {
        private const val SQUARE_FEET_TO_SQUARE_METERS = 10.7639
        //Rate at 21/02/2024
        private const val DOLLAR_TO_EURO = 0.92615
    }

    override fun getLocale(): Locale = locale

    override fun convertSquareFeetToSquareMetersRoundedHalfUp(squareFeet: BigDecimal): BigDecimal =
        squareFeet.divide(BigDecimal(SQUARE_FEET_TO_SQUARE_METERS), 0, RoundingMode.HALF_UP)

    override fun convertSquareMetersToSquareFeetRoundedHalfUp(squareMeters: BigDecimal): BigDecimal =
        squareMeters.multiply(BigDecimal(SQUARE_FEET_TO_SQUARE_METERS)).setScale(0, RoundingMode.HALF_UP)

    override fun convertDollarToEuroRoundedHalfUp(dollar: BigDecimal): BigDecimal =
        dollar.multiply(BigDecimal(DOLLAR_TO_EURO)).setScale(0, RoundingMode.HALF_UP)

    override fun convertEuroToDollarRoundedHalfUp(euro: BigDecimal): BigDecimal =
        euro.divide(BigDecimal(DOLLAR_TO_EURO), 0, RoundingMode.HALF_UP)

    override fun formatRoundedPriceToHumanReadable(price: BigDecimal): String {
        val roundedPrice = price.setScale(0, RoundingMode.HALF_UP)

        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        if (numberFormat.currency?.symbol != "€" && numberFormat.currency?.symbol != "$") {
            return "$$roundedPrice"
        }
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(roundedPrice)
    }

    override fun getLocaleCurrencyFormatting(): CurrencyType = when (locale) {
        Locale.FRANCE -> CurrencyType.EURO
        Locale.US -> CurrencyType.DOLLAR
        else -> CurrencyType.DOLLAR
    }

    override fun getLocaleSurfaceUnitFormatting(): SurfaceUnitType = when (locale) {
        Locale.FRANCE -> SurfaceUnitType.SQUARE_METER
        Locale.US -> SurfaceUnitType.SQUARE_FOOT
        else -> SurfaceUnitType.SQUARE_FOOT
    }
}