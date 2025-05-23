package com.example.studyapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studyapp.database.StudyDatabase
import com.example.studyapp.database.StudySession
import com.example.studyapp.repository.StudyRepository
import com.example.studyapp.viewModel.StudyViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class StudyIntegrationTest {

    private lateinit var db: StudyDatabase
    private lateinit var repository: StudyRepository
    private lateinit var viewModel: StudyViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, StudyDatabase::class.java)
            .allowMainThreadQueries() // only for testing!
            .build()

        repository = StudyRepository(db.sessionDao())
        viewModel = StudyViewModel(context.applicationContext as android.app.Application, repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addAndReadSession() = runBlocking {
        val session = StudySession(subject = "History", durationMinutes = 40, timestamp = System.currentTimeMillis())

        viewModel.addSession(session)
        val stored = viewModel.allSessions.first { it.isNotEmpty() }

        assertEquals(1, stored.size)
        assertEquals("History", stored[0].subject)
    }
    @Test
    fun updateSession_shouldModifySession() = runBlocking {
        val session = StudySession(subject = "Biology", durationMinutes = 30, timestamp = System.currentTimeMillis())
        viewModel.addSession(session)

        val updated = session.copy(subject = "Updated Biology", durationMinutes = 45)
        viewModel.updateSession(updated)

        val result = viewModel.allSessions.first { sessions ->
            sessions.any { it.subject == "Updated Biology" }
        }

        val updatedSession = result.first { it.subject == "Updated Biology" }
        assertEquals("Updated Biology", updatedSession.subject)
        assertEquals(45, updatedSession.durationMinutes)
    }

    @Test
    fun deleteSession_shouldRemoveSession() = runBlocking {
        val session = StudySession(subject = "Art", durationMinutes = 25, timestamp = System.currentTimeMillis())
        viewModel.addSession(session)

        viewModel.deleteSession(session)

        val remaining = viewModel.allSessions.first()
        assertEquals(0, remaining.size)
    }
    @Test
    fun emptyState_shouldReturnZeroValues() = runBlocking {
        val sessions = viewModel.allSessions.first()
        assertEquals(0, sessions.size)

        assertEquals(0, viewModel.currentStreak.first())
        assertEquals(0, viewModel.weeklyStudyMinutes.first())
    }
    @Test
    fun currentStreak_shouldCountConsecutiveDays() = runBlocking {
        val calendar = Calendar.getInstance()

        val today = calendar.timeInMillis
        calendar.add(Calendar.DATE, -1)
        val yesterday = calendar.timeInMillis
        calendar.add(Calendar.DATE, -1)
        val twoDaysAgo = calendar.timeInMillis

        viewModel.addSession(StudySession(subject = "Day1", durationMinutes = 30, timestamp = twoDaysAgo))
        viewModel.addSession(StudySession(subject = "Day2", durationMinutes = 30, timestamp = yesterday))
        viewModel.addSession(StudySession(subject = "Day3", durationMinutes = 30, timestamp = today))

        val streak = viewModel.currentStreak.first { it > 0 }
        assertEquals(3, streak)
    }





}
