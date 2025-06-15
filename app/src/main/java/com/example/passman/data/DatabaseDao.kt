package com.example.passman.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordEntryDao {
    @Query("SELECT * FROM password_entries ORDER BY isFavorite DESC, serviceName ASC")
    fun getAllEntries(): Flow<List<PasswordEntry>>

    @Query("SELECT * FROM password_entries WHERE id = :id")
    fun getEntryById(id: Int): Flow<PasswordEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PasswordEntry)

    @Update
    suspend fun update(entry: PasswordEntry)

    @Query("DELETE FROM password_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)

    @Query("SELECT * FROM password_entries")
    suspend fun getAll(): List<PasswordEntry>
}

