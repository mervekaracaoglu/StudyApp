package com.example.studyapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Update
    suspend fun updateSession(session: StudySession)

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE subject = :subject ORDER BY timestamp DESC")
    fun getSessionsBySubject(subject: String): Flow<List<StudySession>>


    @Query("""
    SELECT SUM(durationMinutes) FROM study_sessions
    WHERE isCompleted = 1 AND date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
""")
    suspend fun getTodayStudyMinutes(): Int?



}