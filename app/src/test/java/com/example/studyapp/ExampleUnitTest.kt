package com.example.studyapp

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.studyapp.database.SessionDao
import com.example.studyapp.database.StudySession
import com.example.studyapp.repository.StudyRepository
import com.example.studyapp.viewModel.StudyViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import io.mockk.every
import junit.framework.TestCase.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class StudyViewModelTest {

    private lateinit var viewModel: StudyViewModel
    private lateinit var sessionDao: SessionDao
    private lateinit var repository: StudyRepository

    @Before
    fun setup() {
        sessionDao = mockk(relaxed = true)
        repository = StudyRepository(sessionDao)
        val context = ApplicationProvider.getApplicationContext<Application>()
        viewModel = StudyViewModel(context, repository)
    }

    @Test
    fun addSession_callsDaoInsertCorrectly() = runTest {
        val session = StudySession(
            subject = "Biology",
            durationMinutes = 25,
            tag = "Reading",
            notes = "Photosynthesis notes",
            isCompleted = true,
            dueDate = null
        )

        coEvery { sessionDao.insertSession(session) } returns Unit

        viewModel.addSession(session)

        coVerify(exactly = 1) { sessionDao.insertSession(session) }
    }

    @Test
    fun deleteSession_callsDaoDeleteCorrectly() = runTest {
        val session = StudySession(
            subject = "History",
            durationMinutes = 30,
            tag = "Review",
            notes = "WWII Notes",
            isCompleted = false,
            dueDate = null
        )

        coEvery { sessionDao.deleteSession(session) } returns Unit

        viewModel.deleteSession(session)

        coVerify(exactly = 1) { sessionDao.deleteSession(session) }
    }

    @Test
    fun updateSession_callsDaoUpdateCorrectly() = runTest {
        val session = StudySession(
            subject = "Math",
            durationMinutes = 45,
            tag = "Homework",
            notes = "Algebra update",
            isCompleted = true,
            dueDate = null
        )

        coEvery { sessionDao.updateSession(session) } returns Unit

        viewModel.updateSession(session)

        coVerify(exactly = 1) { sessionDao.updateSession(session) }
    }
    @Test
    fun currentStreak_computesCorrectly() = runTest {
        val now = System.currentTimeMillis()
        val yesterday = now - 86_400_000
        val twoDaysAgo = now - 2 * 86_400_000

        val sessions = listOf(
            StudySession(subject = "Math", durationMinutes = 30, timestamp = now),
            StudySession(subject = "Bio", durationMinutes = 40, timestamp = yesterday),
            StudySession(subject = "Chem", durationMinutes = 50, timestamp = twoDaysAgo)
        )

        every { sessionDao.getAllSessions() } returns flowOf(sessions)

        viewModel = StudyViewModel(ApplicationProvider.getApplicationContext(), StudyRepository(sessionDao))

        val streak = viewModel.currentStreak.value
        assertEquals(3, streak)
    }

    @Test
    fun weeklyStudyMinutes_computesSumCorrectly() = runTest {
        val now = System.currentTimeMillis()
        val oneDay = 86_400_000

        val sessions = listOf(
            StudySession(subject = "Math", durationMinutes = 40, timestamp = now),
            StudySession(subject = "Biology", durationMinutes = 30, timestamp = now - oneDay),
            StudySession(subject = "Chemistry", durationMinutes = 50, timestamp = now - 3 * oneDay),
            StudySession(subject = "OldSession", durationMinutes = 100, timestamp = now - 10 * oneDay) // should be excluded
        )

        every { sessionDao.getAllSessions() } returns flowOf(sessions)
        viewModel = StudyViewModel(ApplicationProvider.getApplicationContext(), StudyRepository(sessionDao))

        val totalMinutes = viewModel.weeklyStudyMinutes.value
        assertEquals(120, totalMinutes)
    }


}
