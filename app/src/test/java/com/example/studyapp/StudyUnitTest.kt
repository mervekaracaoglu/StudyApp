package com.example.studyapp

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.studyapp.database.StudySession
import com.example.studyapp.repository.StudyRepository
import com.example.studyapp.viewModel.StudyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import org.mockito.kotlin.verify
import java.util.Calendar


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@OptIn(ExperimentalCoroutinesApi::class)
class StudyViewModelTest {

    private lateinit var viewModel: StudyViewModel
    private lateinit var repository: StudyRepository
    private lateinit var appContext: Application

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        appContext = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `allSessions emits data from repository`() = runTest {
        val session = StudySession(
            id = 1,
            subject = "Math",
            durationMinutes = 60,
            timestamp = System.currentTimeMillis()
        )

        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf(session)))

        viewModel = StudyViewModel(application = appContext, repository = repository)

        viewModel.allSessions.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Math", result[0].subject)
        }
    }

    @Test
    fun `deleteSession should remove session from repository`() = runTest {
        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf()))

        val session = StudySession(id = 1, subject = "Art", durationMinutes = 25, timestamp = System.currentTimeMillis())
        whenever(repository.deleteSession(session)).thenReturn(Unit)

        viewModel = StudyViewModel(application = ApplicationProvider.getApplicationContext(), repository = repository)

        viewModel.deleteSession(session)

        verify(repository).deleteSession(session)
    }


    @Test
    fun `loadTodayStudyMinutes should update todayStudyMinutes`() = runTest {

        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf()))

        whenever(repository.getTodayStudyMinutes()).thenReturn(120)

        viewModel = StudyViewModel(appContext, repository)

        viewModel.loadTodayStudyMinutes()

        viewModel.todayStudyMinutes.test {
            assertEquals(120, awaitItem())
        }
    }
    @Test
    fun `setWeeklyGoal updates flow and saves value`() = runTest {
        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf()))

        viewModel = StudyViewModel(ApplicationProvider.getApplicationContext(), repository)

        viewModel.setWeeklyGoal(200)

        viewModel.weeklyGoalMinutes.test {
            assertEquals(200, awaitItem())
        }
    }
    @Test
    fun `weeklyStudyMinutes sums only current week`() = runTest {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.add(Calendar.DATE, -8)
        val lastWeek = calendar.timeInMillis

        val session1 = StudySession(subject = "Now", durationMinutes = 60, timestamp = now)
        val session2 = StudySession(subject = "Old", durationMinutes = 90, timestamp = lastWeek)

        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf(session1, session2)))

        viewModel = StudyViewModel(ApplicationProvider.getApplicationContext(), repository)

        viewModel.weeklyStudyMinutes.test {
            assertEquals(60, awaitItem())
        }
    }
    @Test
    fun `currentStreak stops at non-consecutive day`() = runTest {
        val calendar = Calendar.getInstance()

        val today = calendar.timeInMillis
        calendar.add(Calendar.DATE, -2)
        val twoDaysAgo = calendar.timeInMillis

        val sessionToday = StudySession(subject = "Today", durationMinutes = 30, timestamp = today)
        val sessionTwoDaysAgo = StudySession(subject = "2 Days Ago", durationMinutes = 30, timestamp = twoDaysAgo)

        whenever(repository.getAllSessions()).thenReturn(flowOf(listOf(sessionToday, sessionTwoDaysAgo)))

        viewModel = StudyViewModel(ApplicationProvider.getApplicationContext(), repository)

        viewModel.currentStreak.test {
            assertEquals(1, awaitItem())
        }
    }





}
