package com.example.realestatemanager.domain.property

import assertk.assertThat
import com.emplk.realestatemanager.domain.geocoding.GeocodingRepository
import com.example.realestatemanager.data.estate.EstateRepository
import com.example.realestatemanager.fixtures.getTestEditFormParams
import com.example.realestatemanager.fixtures.testFixedClock
import com.example.realestatemanager.data.geocoding.GeocodingWrapper
import com.example.realestatemanager.domain.estate.AddOrEditEstateUseCase
import com.example.realestatemanager.domain.estate.current.ResetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.media.DeleteMediaUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.fixtures.getTestAddFormParams
import com.example.realestatemanager.ui.estate.add.AddOrEditEvent
import com.example.utils.TestCoroutineRule
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddOrEditPropertyUseCaseTest {

    companion object {
        private const val TEST_EDIT_ESTATE_ID = 1L
        private val TEST_GEOCODING_WRAPPER_SUCCESS = GeocodingWrapper.Success(
            LatLng(123.0, 456.0)
        )
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val estateRepository: EstateRepository = mockk()
    private val geocodingRepository: GeocodingRepository = mockk()
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase = mockk()
    private val resetCurrentEstateIdUseCase: ResetCurrentEstateIdUseCase = mockk()
    private val deleteMediaUseCase: DeleteMediaUseCase = mockk()

    private val addOrEditEstateUseCase = AddOrEditEstateUseCase(
        estateRepository,
        geocodingRepository,
        setNavigationTypeUseCase,
        testFixedClock,
        resetCurrentEstateIdUseCase,
        deleteMediaUseCase,
    )

    @Before
    fun setUp() {
        coEvery { estateRepository.update(any()) } returns true

        coEvery { estateRepository.addEstateWithDetails(any()) } returns true

        coEvery { geocodingRepository.getLatLong(any()) } returns TEST_GEOCODING_WRAPPER_SUCCESS

        coJustRun { deleteMediaUseCase.invoke(TEST_EDIT_ESTATE_ID) }

        justRun { resetCurrentEstateIdUseCase.invoke() }

        justRun { setNavigationTypeUseCase.invoke(any()) }
    }

    @Test
    fun `invoke (add) - nominal case`() = testCoroutineRule.runTest {
        // Given

        // When
        val result = addOrEditEstateUseCase.invoke(getTestAddFormParams())

        // Then
        assertThat { result is AddOrEditEvent.Toast }
        coVerify { setNavigationTypeUseCase.invoke(any()) }
    }

    @Test
    fun `invoke (edit) - nominal case`() = testCoroutineRule.runTest {
        // Given

        // When
        val result = addOrEditEstateUseCase.invoke(getTestEditFormParams(TEST_EDIT_ESTATE_ID))

        // Then
        assertThat { result is AddOrEditEvent.Toast }
        coVerify { setNavigationTypeUseCase.invoke(any()) }
    }

}