package com.example.realestatemanager.data.media.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaDto: MediaDto)

    @Query("SELECT id FROM medias WHERE estate_id = :estateId")
    suspend fun getAllMediasIdsFromEstateId(estateId: Long): List<Long>

    @Query("SELECT * FROM medias")
    fun getAllMediasWithCursor(): Cursor

    @Query("DELETE FROM medias WHERE id = :mediaId")
    suspend fun delete(mediaId: Long)
}