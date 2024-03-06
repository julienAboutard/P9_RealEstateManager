package com.example.realestatemanager.domain.content_resolver

import android.util.Log
import com.example.realestatemanager.data.content.resolver.MediaFileRepository
import javax.inject.Inject

class DeleteFileFromAppFilesUseCase @Inject constructor(
    private val mediaFileRepository: MediaFileRepository,
) {
    suspend fun invoke(absolutePath: String) {
        mediaFileRepository.deleteFromAppFiles(absolutePath)
        Log.i("Deleted media", "Deleted media from app file " + "with absolute path: $absolutePath")
    }
}