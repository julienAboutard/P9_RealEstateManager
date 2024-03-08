package com.example.realestatemanager.data.estate

import com.example.realestatemanager.data.media.MediaMapper
import com.example.realestatemanager.fixtures.getTestMediaEntities
import com.example.realestatemanager.fixtures.getTestEstateDto
import com.example.realestatemanager.fixtures.getTestEstateEntity
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EstateMapperTest {

    private val mediaMapper: MediaMapper = mockk()
    private val estateMapper = EstateMapper(mediaMapper)

    @Before
    fun setUp() {
        every { mediaMapper.mapToDomainEntities(any()) } returns getTestMediaEntities()
    }

    @Test
    fun `mapToDto nominal case`() {
        val result = estateMapper.mapToDto(getTestEstateEntity(1L))

        assertEquals(getTestEstateDto(1L), result)
        confirmVerified(mediaMapper)
    }

    @Test
    fun `mapToDomainEntity nominal case`() {
        val result = estateMapper.mapToDomainEntity(
            getTestEstateDto(1L),
            listOf(mockk())
        )

        assertEquals(getTestEstateEntity(1L), result)
        verify(exactly = 1) {
            mediaMapper.mapToDomainEntities(any())
        }
        confirmVerified(mediaMapper)
    }
}