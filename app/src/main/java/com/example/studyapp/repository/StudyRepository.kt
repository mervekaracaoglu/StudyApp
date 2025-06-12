package com.example.studyapp.repository

import com.example.studyapp.database.SessionDao
import com.example.studyapp.database.StudySession
import kotlinx.coroutines.flow.Flow
/**
 * Repository class that acts as an abstraction layer over the [SessionDao].
 * Handles all data operations related to [StudySession]s.
 *
 * This keeps the ViewModel decoupled from the data source implementation (Room).
 *
 * @property sessionDao The DAO used to perform database operations.
 */
class StudyRepository(private val sessionDao: SessionDao) {
    /**
     * Inserts a new study session into the database.
     *
     * @param session The [StudySession] to insert.
     */
    suspend fun insertSession(session: StudySession) {
        sessionDao.insertSession(session)
    }
    /**
     * Deletes a study session from the database.
     *
     * @param session The [StudySession] to delete.
     */
    suspend fun deleteSession(session: StudySession) {
        sessionDao.deleteSession(session)
    }
    /**
     * Updates an existing study session in the database.
     *
     * @param session The [StudySession] to update.
     */
    suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(session)
    }
    /**
     * Retrieves all study sessions as a [Flow] of a list of [StudySession]s.
     *
     * @return A cold [Flow] that emits the full list of sessions from the database.
     */
    fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getAllSessions()
    }
    /**
     * Calculates the total number of minutes studied today.
     *
     * @return The number of minutes studied today, or 0 if no data is found.
     */
    suspend fun getTodayStudyMinutes(): Int {
        return sessionDao.getTodayStudyMinutes() ?: 0
    }

}