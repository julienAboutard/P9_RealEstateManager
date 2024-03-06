package com.example.realestatemanager.domain.content_resolver

import android.util.Log
import com.example.realestatemanager.data.content.resolver.MediaFileRepository
import javax.inject.Inject

class SaveFileToLocalAppFilesUseCase @Inject constructor(
    private val mediaFileRepository: MediaFileRepository
) {
    companion object {
        private const val PROPERTY_MEDIA_PREFIX = "property_media_"
    }

    suspend fun invoke(stringUri: String, suffix: String): String? =
        mediaFileRepository.saveToAppFiles(stringUri, PROPERTY_MEDIA_PREFIX, suffix).also {
            Log.i("Saved media", "Saved media (string uri: $stringUri) to app file to absolute path: $it")
        }
}