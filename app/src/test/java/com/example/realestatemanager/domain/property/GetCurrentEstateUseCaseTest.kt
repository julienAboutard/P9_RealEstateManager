package com.example.realestatemanager.domain.property

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateIdFlowUseCase
import com.example.realestatemanager.domain.estate.current.GetCurrentEstateUseCase
import com.example.realestatemanager.fixtures.getTestEstateEntity
import com.example.utils.TestCoroutineRule
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentEstateUseCaseTest {

    companion object {
        private const val TEST_PROPERTY_ID = 1L
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val estateRepository: EstateRepository = mockk()
    private val getCurrentEstateIdFlowUseCase: GetCurrentEstateIdFlowUseCase = mockk()

    private val getCurrentEstateUseCase = GetCurrentEstateUseCase(
        estateRepository,
        getCurrentEstateIdFlowUseCase,
    )

    @Before
    fun setUp() {
        every { getCurrentEstateIdFlowUseCase.invoke() } returns flowOf(TEST_PROPERTY_ID)
        every { estateRepository.getEstateByIdAsFlow(TEST_PROPERTY_ID) } returns flowOf(
            getTestEstateEntity(
                TEST_PROPERTY_ID
            )
        )
    }

    @Test
    fun `invoke - nominal case`() = testCoroutineRule.runTest {
        // When
        getCurrentEstateUseCase.invoke().test {
            val capturedPropertyEntity = awaitItem()
            assertThat(capturedPropertyEntity).isEqualTo(getTestEstateEntity(TEST_PROPERTY_ID))
            assertThat(capturedPropertyEntity.id).isEqualTo(TEST_PROPERTY_ID)

            awaitComplete()
            ensureAllEventsConsumed()
        }
        // Then
        coVerify(exactly = 1) { getCurrentEstateIdFlowUseCase.invoke() }
        coVerify(exactly = 1) { estateRepository.getEstateByIdAsFlow(TEST_PROPERTY_ID) }
        confirmVerified(getCurrentEstateIdFlowUseCase)
        confirmVerified(estateRepository)
    }

    @Test
    fun `invoke - property id is null`() = testCoroutineRule.runTest {
        // Given
        every { getCurrentEstateIdFlowUseCase.invoke() } returns flowOf(null)

        getCurrentEstateUseCase.invoke().test {
            // When
            awaitComplete()

            // Then
            coVerify(exactly = 1) { getCurrentEstateIdFlowUseCase.invoke() }
            coVerify(exactly = 0) { estateRepository.getEstateByIdAsFlow(TEST_PROPERTY_ID) }
            confirmVerified(getCurrentEstateIdFlowUseCase, estateRepository)
        }
    }

    @Test
    fun `invoke - current property is null`() = testCoroutineRule.runTest {
        // Given
        every { estateRepository.getEstateByIdAsFlow(TEST_PROPERTY_ID) } returns flowOf(null)

        getCurrentEstateUseCase.invoke().test {
            // When
            awaitComplete()

            // Then
            coVerify(exactly = 1) {
                getCurrentEstateIdFlowUseCase.invoke()
                estateRepository.getEstateByIdAsFlow(TEST_PROPERTY_ID)
            }
            confirmVerified(getCurrentEstateIdFlowUseCase, estateRepository)
        }
    }

    @Test
    fun `invoke - current property id change should be reactive`() = testCoroutineRule.runTest {
        // Given
        every { getCurrentEstateIdFlowUseCase.invoke() } returns flowOf(2L)
        every { estateRepository.getEstateByIdAsFlow(2L) } returns flowOf(getTestEstateEntity(2L))


        // When
        getCurrentEstateUseCase.invoke().test {
            val capturedPropertyEntity = awaitItem()
            assertThat(capturedPropertyEntity).isEqualTo(getTestEstateEntity(2L))
            awaitComplete()
        }

        every { getCurrentEstateIdFlowUseCase.invoke() } returns flowOf(456L)
        every { estateRepository.getEstateByIdAsFlow(456L) } returns flowOf(getTestEstateEntity(456L))

        getCurrentEstateUseCase.invoke().test {
            val capturedPropertyEntity = awaitItem()
            assertThat(capturedPropertyEntity).isEqualTo(getTestEstateEntity(456L))
            awaitComplete()
        }

        // Then
        coVerify(exactly = 2)
        { getCurrentEstateIdFlowUseCase.invoke() }
        coVerify(exactly = 1)
        { estateRepository.getEstateByIdAsFlow(2L) }
        coVerify(exactly = 1)
        { estateRepository.getEstateByIdAsFlow(456L) }
        confirmVerified(getCurrentEstateIdFlowUseCase, estateRepository)
    }
}