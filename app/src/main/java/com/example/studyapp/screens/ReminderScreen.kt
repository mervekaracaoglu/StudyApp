package com.example.studyapp.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import com.example.studyapp.viewModel.ReminderViewModel

@Composable
fun ReminderScreen(viewModel: ReminderViewModel = viewModel()) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var pickedTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    val calendar = Calendar.getInstance().apply { timeInMillis = pickedTimeMillis }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            pickedTimeMillis = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            pickedTimeMillis = calendar.timeInMillis
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Reminder Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { datePicker.show() }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { timePicker.show() }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClick@{
                if (title.isBlank()) {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@onClick
                }
                if (pickedTimeMillis < System.currentTimeMillis()) {
                    Toast.makeText(context, "Time must be in the future", Toast.LENGTH_SHORT).show()
                    return@onClick
                }
                viewModel.scheduleReminder(title, pickedTimeMillis)
                Toast.makeText(context, "Reminder Scheduled", Toast.LENGTH_SHORT).show()
                title = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Reminder")
        }
    }
}
