package com.example.realestatemanager.data.media

import com.example.realestatemanager.data.media.entity.MediaEntity
import com.example.realestatemanager.data.media.room.MediaDto
import javax.inject.Inject

class MediaMapper @Inject constructor() {

    fun mapToDtoEntity(media: MediaEntity, estateId: Long): MediaDto {
        return MediaDto(
            estateId = estateId,
            uri = media.uri,
            isFeatured = media.isFeatured,
            description = media.description,
            type = media.type
        )
    }

    fun mapToDtos(medias: List<MediaEntity>, estateId: Long): List<MediaDto> {
        return medias.map {
            MediaDto(
                estateId = estateId,
                uri = it.uri,
                isFeatured = it.isFeatured,
                description = it.description,
                type = it.type
            )
        }
    }

    fun mapToDomainEntity(mediaDto: MediaDto): MediaEntity {
        return MediaEntity(
            uri = mediaDto.uri,
            isFeatured = mediaDto.isFeatured,
            description = mediaDto.description,
            type = mediaDto.type
        )
    }

    fun mapToDomainEntities(mediaDtoEntities: List<MediaDto>): List<MediaEntity> {
        return mediaDtoEntities.map {
            MediaEntity(
                uri = it.uri,
                isFeatured = it.isFeatured,
                description = it.description,
                type = it.type
            )
        }
    }
}