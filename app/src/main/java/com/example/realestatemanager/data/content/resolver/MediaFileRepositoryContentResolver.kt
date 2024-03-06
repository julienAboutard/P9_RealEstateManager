package com.example.realestatemanager.data.content.resolver

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.time.Clock
import javax.inject.Inject

class MediaFileRepositoryContentResolver @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val clock: Clock,
) : MediaFileRepository {

    /**
     * @return Absolute path of the file where this data is stored (internal app's storage, in "temp" folder)
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    @Throws(IOException::class)
    override suspend fun saveToAppFiles(stringUri: String, filePrefix: String, suffix: String): String? =
        withContext(coroutineDispatcherProvider.io) {
            try {
                val inputStream = contentResolver.openInputStream(Uri.parse(stringUri))
                val cacheFile = File(context.cacheDir, "${filePrefix}${clock.millis()}${suffix}")
                val outputStream = cacheFile.outputStream()
                inputStream?.copyTo(outputStream)
                outputStream.close()
                inputStream?.close()
                return@withContext cacheFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }

    override suspend fun deleteFromAppFiles(absolutePath: String) {
        withContext(coroutineDispatcherProvider.io) {
            if (File(absolutePath).exists()) {
                File(absolutePath).delete()
                Log.d("PictureContentResolver", "File $absolutePath deleted")
            } else Log.d("PictureyContentResolver", "File $absolutePath does not exist")
        }
    }
}