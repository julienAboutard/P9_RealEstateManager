package com.example.realestatemanager.data.estate

import android.database.sqlite.SQLiteException
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.realestatemanager.ensuresDispatcher
import com.example.realestatemanager.fixtures.getTestMediaDto
import com.example.realestatemanager.fixtures.getEstateWithDetail
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.media.MediaMapper
import com.example.realestatemanager.data.media.room.MediaDao
import com.example.realestatemanager.fixtures.getTestEstateDto
import com.example.realestatemanager.fixtures.getTestEstateEntity
import com.example.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EstateRepositoryRoomTest {

    companion object {
        private const val TEST_ESTATE_ID = 1L
        private const val TEST_SQ_LITE_EXCEPTION_MESSAGE = "Test SQLiteException"
        private val GET_ALL_ESTATES_WITH_DETAILS_FLOW: Flow<List<EstateWithDetails>> = flowOf()
        private val GET_ESTATE_WITH_DETAILS_FLOW: Flow<EstateWithDetails> = flowOf()
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val estateDao: EstateDao = mockk()
    private val mediaDao: MediaDao = mockk()
    private val estateMapper: EstateMapper = mockk()
    private val mediaMapper: MediaMapper = mockk()

    private val estateRepositoryRoom = EstateRepositoryRoom(
        estateDao,
        mediaDao,
        estateMapper,
        mediaMapper,
        testCoroutineRule.getTestCoroutineDispatcherProvider()
    )

    @Before
    fun setUp() {
        val testEstateDto = getTestEstateDto(TEST_ESTATE_ID)
        val testMediaDto = getTestMediaDto(TEST_ESTATE_ID)

        every { estateMapper.mapToDto(any()) } returns testEstateDto
        coEvery { estateDao.insert(testEstateDto) }.ensuresDispatcher(
            testCoroutineRule.ioDispatcher,
            answersBlock = {
                TEST_ESTATE_ID
            }
        )

        coEvery {
            estateDao.update(testEstateDto)
        } returns Unit


        every { mediaMapper.mapToDtoEntity(any(), TEST_ESTATE_ID) } returnsMany testMediaDto.map { it }
        coEvery {
            mediaDao.insert(testMediaDto[0])
        } returns 1L

        coEvery {
            mediaDao.insert(testMediaDto[1])
        } returns 2L

        coEvery {
            mediaDao.insert(testMediaDto[2])
        } returns 3L

        coEvery { mediaDao.delete(any()) } returns Unit

        every { estateMapper.mapToDomainEntity(any(), any()) } returns getTestEstateEntity(TEST_ESTATE_ID)
    }

    @Test
    fun `add - nominal case`() = testCoroutineRule.runTest {
        // Given
        val estateEntity = getTestEstateEntity(TEST_ESTATE_ID)

        // When
        val result = estateRepositoryRoom.add(estateEntity)

        // Then
        assertThat(result).isEqualTo(TEST_ESTATE_ID)
        coVerify(exactly = 1) { estateDao.insert(any()) }
        confirmVerified(estateDao)
    }

    @Test
    fun `add with error - throws SQLiteException`() = testCoroutineRule.runTest {
        // Given
        val estateEntity = getTestEstateEntity(TEST_ESTATE_ID)
        coEvery { estateDao.insert(any()) } throws SQLiteException(TEST_SQ_LITE_EXCEPTION_MESSAGE)

        // When
        val result = estateRepositoryRoom.add(estateEntity)

        // Then
        assertThat(result).isNull()
        coVerify(exactly = 1) { estateDao.insert(any()) }
        assertThrows(SQLiteException::class.java) {
            throw SQLiteException(TEST_SQ_LITE_EXCEPTION_MESSAGE)
        }
        confirmVerified(estateDao)
    }

    @Test
    fun `add estate with details - nominal case`() = testCoroutineRule.runTest {
        // Given
        val estateEntity = getTestEstateEntity(TEST_ESTATE_ID)

        // When
        val result = estateRepositoryRoom.addEstateWithDetails(estateEntity)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).isTrue()
        coVerify(exactly = 1) { estateDao.insert(getTestEstateDto(TEST_ESTATE_ID)) }

        coVerifyOrder {
            mediaDao.insert(getTestMediaDto(TEST_ESTATE_ID)[0])
            mediaDao.insert(getTestMediaDto(TEST_ESTATE_ID)[1])
            mediaDao.insert(getTestMediaDto(TEST_ESTATE_ID)[2])
        }
        confirmVerified(estateDao)
    }

    @Test
    fun `add estate with details - error with property insertion`() = testCoroutineRule.runTest {
        // Given
        coEvery { estateDao.insert(any()) } throws SQLiteException(TEST_SQ_LITE_EXCEPTION_MESSAGE)

        // When
        val result = estateRepositoryRoom.addEstateWithDetails(getTestEstateEntity(TEST_ESTATE_ID))

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `get estate as flow - nominal case`() = testCoroutineRule.runTest {
        // Given
        every { estateDao.getEstatesWithDetailsAsFlow() } returns GET_ALL_ESTATES_WITH_DETAILS_FLOW

        val propertyEntity = getTestEstateEntity(TEST_ESTATE_ID)
        every { estateMapper.mapToDomainEntity(any(), any()) } returns propertyEntity

        // When
        estateRepositoryRoom.getEstatesAsFlow().test { awaitComplete() }

        // Then
        coVerify(exactly = 1) { estateDao.getEstatesWithDetailsAsFlow() }
        confirmVerified(estateDao)
    }

    @Test
    fun `get estate by id as flow - nominal case`() = testCoroutineRule.runTest {
        // Given
        every { estateDao.getEstateByIdAsFlow(TEST_ESTATE_ID) } returns GET_ESTATE_WITH_DETAILS_FLOW

        val estateEntity = getTestEstateEntity(TEST_ESTATE_ID)
        every { estateMapper.mapToDomainEntity(any(), any()) } returns estateEntity

        // When
        estateRepositoryRoom.getEstateByIdAsFlow(TEST_ESTATE_ID).test { awaitComplete() }

        // Then
        coVerify(exactly = 1) { estateDao.getEstateByIdAsFlow(TEST_ESTATE_ID) }
        confirmVerified(estateDao)
    }

    @Test
    fun `get estate by id - nominal case`() = testCoroutineRule.runTest {
        //  Given
        coEvery { estateDao.getEstateById(TEST_ESTATE_ID) } returns getEstateWithDetail(TEST_ESTATE_ID)

        // When
        val result = estateRepositoryRoom.getEstateById(TEST_ESTATE_ID)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(getTestEstateEntity(TEST_ESTATE_ID))
        coVerify(exactly = 1) { estateDao.getEstateById(TEST_ESTATE_ID) }
        confirmVerified(estateDao)
    }

    @Test(expected = IllegalStateException::class)
    fun `get estate by id - edge case with null return`() = testCoroutineRule.runTest {
        //  Given
        coEvery { estateDao.getEstateById(TEST_ESTATE_ID) } returns null

        // When
        estateRepositoryRoom.getEstateById(TEST_ESTATE_ID)

        // Then
        assertThrows(IllegalStateException::class.java) {
            throw IllegalStateException("Property with id $TEST_ESTATE_ID not found")
        }
        coVerify(exactly = 1) { estateDao.getEstateById(TEST_ESTATE_ID) }
        confirmVerified(estateDao)
    }

    @Test
    fun `update with error - throws SQLiteException`() = testCoroutineRule.runTest {
        // Given
        coEvery { estateDao.update(any()) } throws SQLiteException(TEST_SQ_LITE_EXCEPTION_MESSAGE)
        val estateEntity = getTestEstateEntity(TEST_ESTATE_ID)

        // When
        val result = estateRepositoryRoom.update(estateEntity)

        // Then
        assertThat(result).isFalse()
        coVerify(exactly = 1) { estateDao.update(any()) }
        assertThrows(SQLiteException::class.java) {
            throw SQLiteException(TEST_SQ_LITE_EXCEPTION_MESSAGE)
        }
        confirmVerified(estateDao)
    }

}