package com.example.realestatemanager.data.estate.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.realestatemanager.data.estate.EstateWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estateDto: EstateDto): Long

    @Transaction
    @Query("SELECT * FROM medias WHERE id = :estateId")
    fun getEstateByIdAsFlow(estateId: Long): Flow<EstateWithDetails?>

    @Transaction
    @Query("SELECT * FROM medias WHERE id = :estateId")
    suspend fun getEstateById(estateId: Long): EstateWithDetails?

    @Transaction
    @Query("SELECT * FROM medias")
    fun getEstatesWithDetailsAsFlow(): Flow<List<EstateWithDetails>>

    @Query("SELECT COUNT(*) FROM medias")
    fun getEstatesCountAsFlow(): Flow<Int>

    @Query("SELECT * FROM medias")
    fun getAllEstatesWithCursor(): Cursor

    @Query("SELECT * FROM medias WHERE id = :estateId")
    fun getEstateByIdWithCursor(estateId: Long): Cursor

    @Update
    suspend fun update(estateDto: EstateDto)
}