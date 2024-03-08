package com.example.realestatemanager.data.filter

import app.cash.turbine.test
import com.example.utils.TestCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class EstatesFilterRepositoryImplTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val repository = EstatesFilterRepositoryImpl()

    @Test
    fun `initial case`() =
        testCoroutineRule.runTest {
            repository.getEstatesFilter().test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `set estates filter`() =
        testCoroutineRule.runTest {
            repository.getEstatesFilter().test {
                // Given
                assertNull(awaitItem())

                // When... and Then
                repository.setEstatesFilter(EstatesFilterEntity(researchDate = null))
                val firstCapturedEmission = awaitItem()
                assertEquals(EstatesFilterEntity(researchDate = null), firstCapturedEmission)

                repository.setEstatesFilter(
                    EstatesFilterEntity(
                        minMaxPrice = Pair(
                            BigDecimal(1),
                            BigDecimal(2)
                        ),
                        researchDate = null,
                    )
                )
                val secondCapturedEmission = awaitItem()
                assertEquals(
                    EstatesFilterEntity(minMaxPrice = Pair(BigDecimal(1), BigDecimal(2)), researchDate = null),
                    secondCapturedEmission
                )
            }
        }

    @Test
    fun `reset estates filter`() = testCoroutineRule.runTest {
        repository.getEstatesFilter().test {
            // Given
            val initialState = awaitItem()
            assertNull(initialState)

            // When
            repository.setEstatesFilter(
                EstatesFilterEntity(
                    minMaxPrice = Pair(BigDecimal(1), BigDecimal(2)), researchDate = null
                )
            )
            val firstCapturedEmission = awaitItem()
            assertEquals(
                EstatesFilterEntity(minMaxPrice = Pair(BigDecimal(1), BigDecimal(2)), researchDate = null),
                firstCapturedEmission
            )

            repository.resetEstatesFilter()

            // Then
            val reinitializeState = awaitItem()
            assertNull(reinitializeState)
        }
    }

    @Test
    fun `set isFiltered`() =
        testCoroutineRule.runTest {
            repository.getIsFiltered().test {
                //Given
                assertNull(awaitItem())

                //When and Then
                repository.setIsFiltered(true)
                assertTrue( awaitItem()!!)

                repository.setIsFiltered(false)
                assertFalse(awaitItem()!!)
            }
        }
}