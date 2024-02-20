package com.example.realestatemanager.domain.media

import com.example.realestatemanager.data.media.MediaRepository
import javax.inject.Inject

class DeleteMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend fun invoke(mediaId: Long) {
        mediaRepository.delete(mediaId)
    }
}