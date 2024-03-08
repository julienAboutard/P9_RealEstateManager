package com.example.realestatemanager.data.content_provider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.realestatemanager.data.content.provider.ContentProvider
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.media.room.MediaDao
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContentProviderTest {
    private lateinit var contentProvider: ContentProvider
    private lateinit var estateDaoTest: EstateDao
    private lateinit var mediaDaoTest: MediaDao
    private lateinit var contentResolver: ContentResolver
    private val testAuthority = "com.example.realestatemanager.data.content_provider.ContentProvider"
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        contentResolver = mockk()

        estateDaoTest = mockk()
        mediaDaoTest = mockk()

        contentProvider = ContentProvider().apply {
            estateDao = estateDaoTest
            mediaDao = mediaDaoTest
        }

        val fakeEstateCursor: Cursor = mockk()
        every { estateDaoTest.getAllEstatesWithCursor() } returns fakeEstateCursor
        justRun {
            fakeEstateCursor.setNotificationUri(any(), any())
        }
    }

    @Test
    fun `query returns Cursor based on URI`() {
        // Given
        val testCursor: Cursor = mockk()
        justRun {
            testCursor.setNotificationUri(
                any(),
                any()
            )
        }
        every { estateDaoTest.getAllEstatesWithCursor() } returns testCursor

        // When
        val uri = Uri.parse("content://$testAuthority/estates")
        val resultCursor = contentProvider.query(uri, null, null, null, null)

        // Then
        verify(exactly = 1) { estateDaoTest.getAllEstatesWithCursor() }
        verify(exactly = 1) { resultCursor.setNotificationUri(any(), any()) }

        assertEquals(testCursor, resultCursor)
    }
}

