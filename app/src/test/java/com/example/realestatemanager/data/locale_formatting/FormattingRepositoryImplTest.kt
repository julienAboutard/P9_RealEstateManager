package com.example.realestatemanager.data.locale_formatting

import com.example.realestatemanager.data.formatting.FormattingRepositoryImpl
import com.example.realestatemanager.data.formatting.type.CurrencyType
import com.example.realestatemanager.data.formatting.type.SurfaceUnitType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

class FormattingRepositoryImplTest {

    private val locale: Locale = Locale.US

    private val formattingRepositoryImpl = FormattingRepositoryImpl(locale)

    @Test
    fun `getLocale() - nominal case`() {
        // When
        val result = formattingRepositoryImpl.getLocale()

        // Then
        assertEquals(locale, result)
    }

    @Test
    fun `convertSquareFeetToSquareMetersRoundedHalfUp() - nominal case`() {
        // When
        val result = formattingRepositoryImpl.convertSquareFeetToSquareMetersRoundedHalfUp(BigDecimal(10))

        // Then
        assertEquals(BigDecimal.ONE, result)
    }

    @Test
    fun `convertSquareMetersToSquareFeetRoundedHalfUp() - nominal case`() {
        // When
        val result = formattingRepositoryImpl.convertSquareMetersToSquareFeetRoundedHalfUp(BigDecimal(10))

        // Then
        assertEquals(BigDecimal(108), result)
    }

    @Test
    fun `convertDollarToEuroRoundedHalfUp() - nominal case`() {
        // When
        val result = formattingRepositoryImpl.convertDollarToEuroRoundedHalfUp(BigDecimal(10))

        // Then
        assertEquals(BigDecimal(9), result)
    }

    @Test
    fun `convertEuroToDollarRoundedHalfUp() - nominal case`() {
        // When
        val result = formattingRepositoryImpl.convertEuroToDollarRoundedHalfUp(BigDecimal(10))

        // Then
        assertEquals(BigDecimal(11), result)
    }

    @Test
    fun `formatRoundedPriceToHumanReadable() - case Locale US`() {
        // When
        val result = formattingRepositoryImpl.formatRoundedPriceToHumanReadable(BigDecimal(10))

        // Then
        assertEquals("$10", result)
    }

    @Test
    fun `formatRoundedPriceToHumanReadable() - case Locale FR`() {
        // Given
        val frenchLocale = Locale.FRANCE
        val humanReadableRepositoryImpl = FormattingRepositoryImpl(frenchLocale)

        // When
        val result = humanReadableRepositoryImpl.formatRoundedPriceToHumanReadable(BigDecimal(10))

        // Then
        assertEquals("10 €", result)
    }

    @Test
    fun `formatRoundedPriceToHumanReadable() - case Locale other than US or FR`() {
        // Given
        val taiwaneseLocale = Locale.TAIWAN
        val formattingRepositoryImpl = FormattingRepositoryImpl(taiwaneseLocale)

        // When
        val result = formattingRepositoryImpl.formatRoundedPriceToHumanReadable(BigDecimal(10))

        // Then
        assertEquals("$10", result)
    }

    @Test
    fun `getLocaleCurrencyFormatting() - case Locale US`() {
        // When
        val result = formattingRepositoryImpl.getLocaleCurrencyFormatting()

        // Then
        assertEquals(CurrencyType.DOLLAR, result)
    }

    @Test
    fun `getLocaleCurrencyFormatting() - case Locale FR`() {
        // Given
        val frenchLocale = Locale.FRANCE
        val formattingRepositoryImpl = FormattingRepositoryImpl(frenchLocale)

        // When
        val result = formattingRepositoryImpl.getLocaleCurrencyFormatting()

        // Then
        assertEquals(CurrencyType.EURO, result)
    }

    @Test
    fun `getLocaleCurrencyFormatting() - case Locale other than US or FR`() {
        // Given
        val taiwaneseLocale = Locale.TAIWAN
        val formattingRepositoryImpl = FormattingRepositoryImpl(taiwaneseLocale)

        // When
        val result = formattingRepositoryImpl.getLocaleCurrencyFormatting()

        // Then
        assertEquals(CurrencyType.DOLLAR, result)
    }

    @Test
    fun `getLocaleSurfaceUnitFormatting() - case Locale US`() {
        // When
        val result = formattingRepositoryImpl.getLocaleSurfaceUnitFormatting()

        // Then
        assertEquals(SurfaceUnitType.SQUARE_FOOT, result)
    }

    @Test
    fun `getLocaleSurfaceUnitFormatting() - case Locale FR`() {
        // Given
        val frenchLocale = Locale.FRANCE
        val formattingRepositoryImpl = FormattingRepositoryImpl(frenchLocale)

        // When
        val result = formattingRepositoryImpl.getLocaleSurfaceUnitFormatting()

        // Then
        assertEquals(SurfaceUnitType.SQUARE_METER, result)
    }

    @Test
    fun `getLocaleSurfaceUnitFormatting() - case Locale other than US or FR`() {
        // Given
        val taiwaneseLocale = Locale.TAIWAN
        val formattingRepositoryImpl = FormattingRepositoryImpl(taiwaneseLocale)

        // When
        val result = formattingRepositoryImpl.getLocaleSurfaceUnitFormatting()

        // Then
        assertEquals(SurfaceUnitType.SQUARE_FOOT, result)
    }
}