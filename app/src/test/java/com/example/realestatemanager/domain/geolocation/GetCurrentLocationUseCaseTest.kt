package com.example.realestatemanager.domain.geolocation

import app.cash.turbine.test
import com.example.realestatemanager.R
import com.example.realestatemanager.data.connectivity.GpsConnectivityRepository
import com.example.realestatemanager.data.geolocation.GeolocationRepository
import com.example.realestatemanager.data.geolocation.GeolocationState
import com.example.realestatemanager.domain.permission.HasLocationPermissionFlowUseCase
import com.example.realestatemanager.ui.utils.NativeText
import com.example.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentLocationUseCaseTest {

    companion object {
        private const val TEST_LATITUDE = 123.0
        private const val TEST_LONGITUDE = 456.0
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val geolocationRepository: GeolocationRepository = mockk()
    private val gpsConnectivityRepository: GpsConnectivityRepository = mockk()
    private val hasLocationPermissionFlowUseCase: HasLocationPermissionFlowUseCase = mockk()

    private val getCurrentLocationUseCase = GetCurrentLocationUseCase(
        hasLocationPermissionFlowUseCase,
        gpsConnectivityRepository,
        geolocationRepository
    )

    @Before
    fun setUp() {
        coEvery { hasLocationPermissionFlowUseCase.invoke() } returns flowOf(true)
        coEvery { gpsConnectivityRepository.isGpsEnabledAsFlow() } returns flowOf(true)
        coEvery { geolocationRepository.getCurrentLocationAsFlow() } returns flowOf(
            GeolocationState.Success(
                latitude = TEST_LATITUDE,
                longitude = TEST_LONGITUDE,
            )
        )
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // When
        getCurrentLocationUseCase.invoke().test {

            // Then
            val result = awaitItem()
            assertTrue(result is GeolocationState.Success)
            assertEquals(TEST_LATITUDE, (result as GeolocationState.Success).latitude, 0.0)
            assertEquals(TEST_LONGITUDE, result.longitude, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `edge case`() = testCoroutineRule.runTest {
        // Given
        coEvery { hasLocationPermissionFlowUseCase.invoke() } returns flowOf(false)
        coEvery { gpsConnectivityRepository.isGpsEnabledAsFlow() } returns flowOf(false)
        coEvery { geolocationRepository.getCurrentLocationAsFlow() } returns flowOf(
            GeolocationState.Error(null)
        )

        // When
        getCurrentLocationUseCase.invoke().test {

            // Then
            val result = awaitItem()
            assertTrue(result is GeolocationState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no permission given`() = testCoroutineRule.runTest {
        // Given
        coEvery { hasLocationPermissionFlowUseCase.invoke() } returns flowOf(false)

        // When
        getCurrentLocationUseCase.invoke().test {

            // Then
            val result = awaitItem()
            assertTrue(result is GeolocationState.Error)
            assertNull((result as GeolocationState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no GPS connection`() = testCoroutineRule.runTest {
        // Given
        coEvery { gpsConnectivityRepository.isGpsEnabledAsFlow() } returns flowOf(false)

        // When
        getCurrentLocationUseCase.invoke().test {

            // Then
            val result = awaitItem()
            assertTrue(result is GeolocationState.Error)
            assertEquals(
                NativeText.Resource(R.string.geolocation_error_no_gps),
                (result as GeolocationState.Error).message
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}