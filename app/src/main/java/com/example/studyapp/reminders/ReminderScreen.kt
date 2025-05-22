package com.example.studyapp.reminders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import java.util.Date

@Composable
fun ReminderScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as android.app.Application
    val viewModel: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(app))

    val reminders by viewModel.reminders.collectAsState()

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isRepeating by remember { mutableStateOf(false) }

    var calendar by remember {
        mutableStateOf(Calendar.getInstance().apply { add(Calendar.SECOND, 30) })
    }

    val showDateTimePicker = {
        val now = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            TimePickerDialog(context, { _, hour, minute ->
                calendar = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute)
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = showDateTimePicker) {
            Text("Pick Date & Time")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isRepeating, onCheckedChange = { isRepeating = it })
            Text("Repeat Daily")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.scheduleReminder(title, message, calendar.timeInMillis, isRepeating)
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Save Reminder")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Scheduled Reminders", style = MaterialTheme.typography.titleMedium)
        reminders.forEach { reminder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = reminder.title)
                    Text(text = Date(reminder.timeInMillis).toString(), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { viewModel.cancelReminder(reminder) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
                }
            }
        }
    }
}
