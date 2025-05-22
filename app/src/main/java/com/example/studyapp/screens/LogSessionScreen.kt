package com.example.studyapp.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studyapp.R
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogSessionScreen(
    viewModel: StudyViewModel,
    onSessionSaved: () -> Unit = {}
) {
    var subject by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var dueDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                dueDateMillis = calendar.timeInMillis
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    val dueDateFormatted = dueDateMillis?.let {
        SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date(it))
    } ?: stringResource(R.string.no_due_date)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.log_study_session), style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text(stringResource(R.string.subject)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text(stringResource(R.string.duration_minutes)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            label = { Text(stringResource(R.string.tag_optional)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.notes_optional)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.completed))
        }

        Text(stringResource(R.string.due_date_label, dueDateFormatted))

        Button(onClick = { showDatePicker = true }) {
            Text(stringResource(R.string.pick_due_date_time))
        }

        Button(
            onClick = {
                val durationInt = duration.toIntOrNull()
                if (subject.isNotBlank() && durationInt != null) {
                    val session = StudySession(
                        subject = subject.trim(),
                        durationMinutes = durationInt,
                        tag = tag.takeIf { it.isNotBlank() },
                        notes = notes.takeIf { it.isNotBlank() },
                        dueDate = dueDateMillis,
                        isCompleted = isCompleted
                    )
                    viewModel.addSession(session)
                    subject = ""
                    duration = ""
                    tag = ""
                    notes = ""
                    dueDateMillis = null
                    isCompleted = false
                    onSessionSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_session))
        }
    }
}
