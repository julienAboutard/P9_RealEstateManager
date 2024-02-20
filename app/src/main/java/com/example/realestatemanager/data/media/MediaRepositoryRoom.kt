package com.example.realestatemanager.data.media

import com.example.realestatemanager.data.media.room.MediaDao
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepositoryRoom @Inject constructor(
    private val mediaDao: MediaDao,
    private val  coroutineDispatcherProvider: CoroutineDispatcherProvider
): MediaRepository {

    override suspend fun getMediasIds(estateId: Long): List<Long> = withContext(coroutineDispatcherProvider.io){
        mediaDao.getAllMediasIdsFromEstateId(estateId)
    }

    override suspend fun delete(mediaId: Long) = withContext(coroutineDispatcherProvider.io) {
        mediaDao.delete(mediaId)
    }
}