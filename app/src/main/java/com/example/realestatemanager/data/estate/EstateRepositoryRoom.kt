package com.example.realestatemanager.data.estate

import android.database.sqlite.SQLiteException
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.realestatemanager.data.estate.entity.EstateEntity
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.media.MediaMapper
import com.example.realestatemanager.data.media.room.MediaDao
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EstateRepositoryRoom @Inject constructor(
    private val estateDao: EstateDao,
    private val mediaDao: MediaDao,
    private val estateMapper: EstateMapper,
    private val mediaMapper: MediaMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : EstateRepository {

    override suspend fun add(estateEntity: EstateEntity): Long? = withContext(coroutineDispatcherProvider.io) {
        try {
            estateDao.insert(estateMapper.mapToDto(estateEntity))
        } catch (e: SQLiteException) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun addEstateWithDetails(estateEntity: EstateEntity): Boolean = withContext(coroutineDispatcherProvider.io) {

        try {
            val estateId = add(estateEntity) ?: return@withContext false

            estateEntity.medias.map {
                mediaDao.insert(mediaMapper.mapToDtoEntity(it, estateId))
            }
            true
        } catch (e: SQLiteException) {
            e.printStackTrace()
            false
        }
    }

    override fun getEstatesAsFlow(): Flow<List<EstateEntity>> = estateDao
        .getEstatesWithDetailsAsFlow()
        .map { estateWithDetailsList: List<EstateWithDetails> ->
            estateWithDetailsList.mapNotNull { estateWithDetails ->
                estateMapper.mapToDomainEntity(
                    estateWithDetails.estate,
                    estateWithDetails.medias
                )
            }
        }
        .flowOn(coroutineDispatcherProvider.io)

    override fun getEstatesCountAsFlow(): Flow<Int> = estateDao.getEstatesCountAsFlow()

    override fun getEstateByIdAsFlow(estateId: Long): Flow<EstateEntity?> = estateDao
        .getEstateByIdAsFlow(estateId)
        .map {
            it.let { estateWithDetails ->
                estateMapper.mapToDomainEntity(
                    estateWithDetails.estate,
                    estateWithDetails.medias
                )
            }
        }
        .flowOn(coroutineDispatcherProvider.io)

    override suspend fun getEstateById(estateId: Long): EstateEntity = withContext(coroutineDispatcherProvider.io) {
        estateDao.getEstateById(estateId)?.let {
            estateMapper.mapToDomainEntity(
                it.estate,
                it.medias
            )
        }
    } ?: throw IllegalStateException("Property with id $estateId not found")

    override suspend fun update(estateEntity: EstateEntity): Boolean = withContext(coroutineDispatcherProvider.io) {
        try {
            estateDao.update(estateMapper.mapToDto(estateEntity))

            estateEntity.medias.map {
                mediaDao.insert(mediaMapper.mapToDtoEntity(it, estateEntity.id))
            }
            true
        } catch (e: SQLiteException) {
            e.printStackTrace()
            false
        }
    }
}