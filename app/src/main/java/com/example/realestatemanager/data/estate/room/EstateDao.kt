package com.example.realestatemanager.data.estate.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.realestatemanager.data.estate.EstateWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estateDto: EstateDto): Long

    @Transaction
    @Query("SELECT * FROM estates WHERE id = :estateId")
    fun getEstateByIdAsFlow(estateId: Long): Flow<EstateWithDetails>

    @Transaction
    @Query("SELECT * FROM estates WHERE id = :estateId")
    suspend fun getEstateById(estateId: Long): EstateWithDetails?

    @Transaction
    @Query("SELECT * FROM estates")
    fun getEstatesWithDetailsAsFlow(): Flow<List<EstateWithDetails>>

    @Query("SELECT COUNT(*) FROM estates")
    fun getEstatesCountAsFlow(): Flow<Int>

    @Query("SELECT * FROM estates")
    fun getAllEstatesWithCursor(): Cursor

    @Query("SELECT * FROM estates WHERE id = :estateId")
    fun getEstateByIdWithCursor(estateId: Long): Cursor

    @Update
    suspend fun update(estateDto: EstateDto)
}