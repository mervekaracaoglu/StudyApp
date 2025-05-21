package com.example.studyapp.repository

import com.example.studyapp.database.SessionDao
import com.example.studyapp.database.StudySession
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val sessionDao: SessionDao) {
    suspend fun insertSession(session: StudySession) {
        sessionDao.insertSession(session)
    }
    suspend fun deleteSession(session: StudySession) {
        sessionDao.deleteSession(session)
    }
    suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(session)
    }

    fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getAllSessions()
    }

    fun getSessionsBySubject(subject: String): Flow<List<StudySession>> {
        return sessionDao.getSessionsBySubject(subject)
    }

}