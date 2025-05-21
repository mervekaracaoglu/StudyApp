package com.example.studyapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel

@Composable
fun LogSessionScreen(
    viewModel: StudyViewModel,
    onSessionSaved: () -> Unit = {}
) {
    var subject by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Log a Study Session", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (minutes)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            label = { Text("Tag (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val durationInt = duration.toIntOrNull()
                if (subject.isNotBlank() && durationInt != null) {
                    val session = StudySession(
                        subject = subject.trim(),
                        durationMinutes = durationInt,
                        tag = tag.takeIf { it.isNotBlank() },
                        notes = notes.takeIf { it.isNotBlank() }
                    )
                    viewModel.addSession(session)
                    subject = ""
                    duration = ""
                    tag = ""
                    notes = ""
                    onSessionSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Session")
        }
    }
}
