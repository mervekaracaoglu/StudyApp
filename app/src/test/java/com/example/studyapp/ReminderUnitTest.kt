package com.example.studyapp.reminders

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderViewModelTest {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var dao: ReminderDao

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reminders emits data from DAO`() = runTest {
        val sampleData = listOf(ReminderEntity(1, "Test", "Message", 123456789L, false))
        whenever(dao.getAll()).thenReturn(sampleData)

        viewModel = ReminderViewModel(app = mock(), dao = dao)

        viewModel.reminders.test {
            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("Test", emission[0].title)
        }
    }
}
