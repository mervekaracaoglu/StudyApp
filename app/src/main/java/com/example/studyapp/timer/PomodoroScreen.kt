package com.example.studyapp.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.R
import java.util.Locale

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = viewModel(factory = PomodoroViewModelFactory(LocalContext.current))
) {
    val state by viewModel.state.collectAsState()

    val minutes = (state.timeLeftMillis / 1000) / 60
    val seconds = (state.timeLeftMillis / 1000) % 60
    val timeText = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                state.isBreak && state.isLongBreak -> stringResource(R.string.long_break)
                state.isBreak -> stringResource(R.string.short_break)
                else -> stringResource(R.string.focus_time)
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val action = if (state.isRunning) PomodoroService.ACTION_PAUSE else PomodoroService.ACTION_START
                viewModel.controlForegroundService(action)
            }) {
                Text(
                    text = if (state.isRunning)
                        stringResource(R.string.pause)
                    else
                        stringResource(R.string.start)
                )
            }

            Button(onClick = {
                viewModel.controlForegroundService(PomodoroService.ACTION_RESET)
            }) {
                Text(stringResource(R.string.reset))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.sessions_completed, state.sessionCount)
        )
    }
}
