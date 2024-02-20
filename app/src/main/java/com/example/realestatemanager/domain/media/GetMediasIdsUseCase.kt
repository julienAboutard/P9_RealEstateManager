package com.example.realestatemanager.domain.media

import com.example.realestatemanager.data.media.MediaRepository
import javax.inject.Inject

class GetMediasIdsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {

    suspend fun invoke(estateId: Long): List<Long> = mediaRepository.getMediasIds(estateId)
}