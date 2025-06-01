package com.example.studyapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studyapp.R
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedSessionsScreen(
    viewModel: StudyViewModel,
    navController: NavController
) {
    val allSessions by viewModel.allSessions.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showCompletedOnly by remember { mutableStateOf(false) }
    var filterByDueDate by remember { mutableStateOf(false) }

    val filteredSessions = allSessions.filter {
        (!showCompletedOnly || it.isCompleted) &&
                (!filterByDueDate || it.dueDate != null && it.dueDate >= System.currentTimeMillis())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.logged_sessions)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = showCompletedOnly,
                    onClick = { showCompletedOnly = !showCompletedOnly },
                    label = { Text(stringResource(R.string.completed_only)) }
                )
                FilterChip(
                    selected = filterByDueDate,
                    onClick = { filterByDueDate = !filterByDueDate },
                    label = { Text(stringResource(R.string.future_due_date)) }
                )
            }

            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_sessions_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredSessions) { session ->
                        val confirmText = stringResource(R.string.confirm)
                        val deleteText = stringResource(R.string.delete_session_confirm)
                        SessionItem(
                            session = session,
                            onDelete = {
                                coroutineScope.launch {
                                    val result = snackBarHostState.showSnackbar(
                                        message = deleteText,
                                        actionLabel = confirmText
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.deleteSession(session)
                                    }
                                }
                            },
                            onEdit = {
                                navController.navigate("editSession/${session.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

//convert long value in ms to formatted date/time string
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun SessionItem(
    session: StudySession,
    onDelete: (StudySession) -> Unit,
    onEdit: (StudySession) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(session) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = session.subject,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { onDelete(session) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.duration_minutes, session.durationMinutes),
                style = MaterialTheme.typography.bodyMedium
            )
            session.tag?.let {
                Text(
                    text = stringResource(R.string.tag_label, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            session.notes?.let {
                Text(
                    text = stringResource(R.string.notes_label, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = stringResource(R.string.date_label, formatTimestamp(session.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
            if (session.isCompleted) {
                Text(
                    text = stringResource(R.string.completed_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            session.dueDate?.let {
                Text(
                    text = stringResource(R.string.due_label, formatTimestamp(it)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}
