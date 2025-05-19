package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.SessionViewModel
import com.example.studyapp.ui.theme.StudyAppTheme

/**
 * Main activity of the StudyApp.
 */
class MainActivity : ComponentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LogSessionScreen(
                        viewModel = sessionViewModel,
                        onSessionSaved = {
                            // You could navigate or show a Snackbar here
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Composable screen for logging a study session.
 */
@Composable
fun LogSessionScreen(
    viewModel: SessionViewModel,
    onSessionSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subject by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Log a Study Session", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val validDuration = duration.toIntOrNull()
                if (subject.isNotBlank() && validDuration != null) {
                    val session = StudySession(
                        subject = subject.trim(),
                        durationMinutes = validDuration,
                        tag = tag.takeIf { it.isNotBlank() },
                        notes = notes.takeIf { it.isNotBlank() }
                    )
                    viewModel.addSession(session)
                    onSessionSaved()
                    subject = ""
                    duration = ""
                    tag = ""
                    notes = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Session")
        }
    }
}
