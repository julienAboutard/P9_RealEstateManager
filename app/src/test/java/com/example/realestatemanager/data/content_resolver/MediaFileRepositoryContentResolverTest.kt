package com.example.realestatemanager.data.content_resolver

import android.content.ContentResolver
import android.content.Context
import com.example.realestatemanager.data.content.resolver.MediaFileRepositoryContentResolver
import com.example.realestatemanager.fixtures.testFixedClock
import com.example.utils.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MediaFileRepositoryContentResolverTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val contentResolver: ContentResolver = mockk()
    private val context: Context = mockk()

    private val mediaFileRepositoryContentResolver = MediaFileRepositoryContentResolver(
        contentResolver,
        context,
        testCoroutineRule.getTestCoroutineDispatcherProvider(),
        testFixedClock
    )

    @Before
    fun setUp() {
        every { contentResolver.openInputStream(any()) } returns mockk()
        every { contentResolver.openInputStream(any())?.copyTo(any()) } returns mockk()
        every { contentResolver.openInputStream(any())?.close() } returns Unit
        every { context.cacheDir } returns mockk()
        every { context.cacheDir.absolutePath } returns "cacheDir"
    }

     /*@Test
     fun `initial case`() = testCoroutineRule.runTest {

         // Given
         val stringUri = "stringUri"
         val filePrefix = "filePrefix"
         val fileSuffix = ".jpg"
         val absolutePath = "cacheDir/filePrefix0.jpg"

         // When
         val result = mediaFileRepositoryContentResolver.saveToAppFiles(stringUri, filePrefix, fileSuffix)

         // Then
         assertEquals(absolutePath, result)
     }*/
}