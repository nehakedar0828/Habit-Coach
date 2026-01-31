package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitcoachai.data.local.db.HabitDatabase
import com.example.habitcoachai.data.local.entity.HabitEntity
import com.example.habitcoachai.viewmodel.HabitViewModel
import com.example.habitcoachai.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/* ---------------- COLORS ---------------- */

private val BackgroundDark = Color(0xFF0E141B)
private val CardDark = Color(0xFF1B2330)
private val PrimaryBlue = Color(0xFF7DD3FC)
private val SecondaryBlue = Color(0xFF38BDF8)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextSecondary = Color(0xFF94A3B8)
private val SuccessGreen = Color(0xFF22C55E)

/* ---------------- DASHBOARD ---------------- */

@Composable
fun DashboardScreen(userName: String?) {

    var showAddDialog by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }

    val context = LocalContext.current
    val database = remember { HabitDatabase.getDatabase(context) }

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(database)
    )

    val habits by habitViewModel.habits.collectAsState(initial = emptyList())
    val completedIds by habitViewModel
        .getCompletedHabitIdsForDate(selectedDate.toString())
        .collectAsState(initial = emptyList())

    val greetingText = if (userName.isNullOrBlank())
        "Welcome 👋"
    else
        "Welcome, $userName 👋"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {

        Column(modifier = Modifier.padding(top = 26.dp)) {

            /* HEADER */

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    SecondaryBlue.copy(alpha = 0.25f),
                                    CardDark
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "HabitCoachAI",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Build habits. Stay consistent.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = greetingText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Your Habits",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateStrip(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(
                        habit = habit,
                        isChecked = habit.id in completedIds,
                        onCheckedChange = { checked ->
                            habitViewModel.toggleHabitForDate(
                                habit,
                                selectedDate.toString(),
                                checked
                            )
                        },
                        onDelete = { habitToDelete = habit }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SecondaryBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "+",
                fontSize = 28.sp,
                color = Color.White
            )
        }
    }

    /* ADD HABIT */

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add New Habit",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            },
            text = {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Habit name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (habitName.isNotBlank()) {
                            habitViewModel.addHabit(habitName)
                            habitName = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add Habit")
                }
            }
        )
    }

    /* DELETE CONFIRMATION */

    if (habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = {
                Text(
                    text = "Delete Habit?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete this habit?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        habitViewModel.deleteHabit(habitToDelete!!)
                        habitToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ---------------- HABIT CARD ---------------- */

@Composable
fun HabitCard(
    habit: HabitEntity,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    uncheckedColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = habit.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isChecked) TextSecondary else TextPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextSecondary
                )
            }
        }
    }
}

/* ---------------- DATE STRIP ---------------- */

@Composable
fun DateStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val dates = remember { (-7..7).map { today.plusDays(it.toLong()) } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        dates.forEach { date ->
            val isSelected = date == selectedDate

            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) SecondaryBlue else CardDark)
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    ),
                    fontSize = 12.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = date.dayOfMonth.toString(),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
