package com.example.studyapp.reminders

import androidx.room.*

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
