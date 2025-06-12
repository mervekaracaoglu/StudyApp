package com.example.studyapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
/**
 * Data Access Object (DAO) for managing [StudySession] entries in the Room database.
 * Provides methods for inserting, updating, deleting, and querying study sessions.
 */
@Dao
interface SessionDao {
    /**
     * Inserts a new study session into the database.
     * If a conflict occurs (e.g. same primary key), the existing entry will be replaced.
     *
     * @param session The study session to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession)

    /**
     * Deletes a study session from the database.
     *
     * @param session The session to delete.
     */
    @Delete
    suspend fun deleteSession(session: StudySession)

    /**
     * Updates an existing study session in the database.
     *
     * @param session The session with updated fields.
     */
    @Update
    suspend fun updateSession(session: StudySession)

    /**
     * Retrieves all study sessions from the database, ordered by timestamp descending.
     *
     * @return A [Flow] that emits a list of all study sessions.
     */
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    /**
     * Calculates the total study duration (in minutes) for all completed sessions
     * that occurred on the current day.
     *
     * @return The total minutes studied today, or `null` if none found.
     */
    @Query("""
    SELECT SUM(durationMinutes) FROM study_sessions
    WHERE isCompleted = 1 AND date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
""")
    suspend fun getTodayStudyMinutes(): Int?



}