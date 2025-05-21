package com.example.studyapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedSessionsScreen(
    viewModel: StudyViewModel,
    navController: NavController
) {
    val allSessions by viewModel.allSessions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
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
                title = { Text("Logged Sessions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showCompletedOnly,
                    onClick = { showCompletedOnly = !showCompletedOnly },
                    label = { Text("Completed Only") }
                )
                FilterChip(
                    selected = filterByDueDate,
                    onClick = { filterByDueDate = !filterByDueDate },
                    label = { Text("Future Due Date") }
                )
            }

            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No sessions match the filters.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredSessions) { session ->
                        SessionItem(
                            session = session,
                            onDelete = {
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Delete this session?",
                                        actionLabel = "Confirm"
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
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEdit(session) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(session.subject, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onDelete(session) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            Text("Duration: ${session.durationMinutes} mins")
            session.tag?.let { Text("Tag: $it") }
            session.notes?.let { Text("Notes: $it") }
            Text("Date: ${formatTimestamp(session.timestamp)}")
            if (session.isCompleted) Text("✔ Completed")
            session.dueDate?.let {
                Text("Due: ${formatTimestamp(it)}")
            }
        }
    }
}
