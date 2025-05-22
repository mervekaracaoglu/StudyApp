package com.example.studyapp.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.studyapp.R
import com.example.studyapp.auth.AuthViewModel
import com.example.studyapp.viewModel.StudyViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel = viewModel(),
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current)),
    studyViewModel: StudyViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val authError by authViewModel.authError.collectAsState()
    val displayName by authViewModel.userDisplayName.collectAsState()
    val photoUrl by authViewModel.userPhotoUrl.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authError) {
        authError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(24.dp))

            // Theme toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.dark_mode))
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.user_section), style = MaterialTheme.typography.titleMedium)

            if (isLoggedIn) {
                photoUrl?.let {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(72.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                Text(stringResource(R.string.name_label, displayName ?: stringResource(R.string.unknown)))
                Text(stringResource(R.string.email_label, userEmail ?: stringResource(R.string.unknown)))

                Button(onClick = { authViewModel.logout() }) {
                    Text(stringResource(R.string.log_out))
                }
            } else {
                Button(onClick = {
                    coroutineScope.launch {
                        authViewModel.signInWithGoogle {
                            studyViewModel.loadSessionsFromFirestore()
                        }
                    }
                }) {
                    Text(stringResource(R.string.log_in_with_google))
                }
            }
        }
    }
}
