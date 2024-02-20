package com.example.realestatemanager.data.media

interface MediaRepository {

    suspend fun getMediasIds(estateId: Long): List<Long>
    suspend fun delete(mediaId: Long)
}