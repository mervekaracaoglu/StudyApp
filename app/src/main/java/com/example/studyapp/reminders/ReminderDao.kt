package com.example.studyapp.reminders

import androidx.room.*
/**
 * Data Access Object (DAO) for accessing and managing [ReminderEntity] entries in the database.
 */
@Dao
interface ReminderDao {

    /**
     * Retrieves all saved reminders from the database.
     *
     * @return A list of all [ReminderEntity] objects.
     */
    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<ReminderEntity>

    /**
     * Inserts a reminder into the database. If a reminder with the same ID exists, it will be replaced.
     *
     * @param reminder The [ReminderEntity] to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    /**
     * Deletes a specific reminder from the database.
     *
     * @param reminder The [ReminderEntity] to delete.
     */
    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
