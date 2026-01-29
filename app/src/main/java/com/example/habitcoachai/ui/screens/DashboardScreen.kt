package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/* -------------------- 🎨 BLUE THEME COLORS -------------------- */

private val BackgroundDark = Color(0xFF0E141B)
private val CardDark = Color(0xFF1B2330)
private val PrimaryBlue = Color(0xFF7DD3FC)
private val SecondaryBlue = Color(0xFF38BDF8)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextSecondary = Color(0xFF94A3B8)
private val SuccessGreen = Color(0xFF22C55E)

/* -------------------- 🏠 DASHBOARD SCREEN -------------------- */

@Composable
fun DashboardScreen(userName: String?) {

    var showDialog by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val database = remember { HabitDatabase.getDatabase(context) }

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(database)
    )

    val habits by habitViewModel.habits.collectAsState(initial = emptyList())

    val greetingText = if (userName.isNullOrBlank()) {
        "Welcome 👋"
    } else {
        "Welcome, $userName 👋"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {

        Column {

            /* ---------------- 🧠 HABITCOACHAI HEADER CARD ---------------- */

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
                                colors = listOf(
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

            /* ---------------- 📌 SECTION TITLE ---------------- */

            Text(
                text = "Your Habits",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------------- 📋 HABIT LIST ---------------- */

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(
                        habit = habit,
                        onCheckedChange = { checked ->
                            habitViewModel.toggleHabitCompletion(habit, checked)
                        }
                    )
                }
            }
        }

        /* ---------------- ➕ FAB ---------------- */

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = SecondaryBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "+",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    /* ---------------- ➕ ADD HABIT DIALOG ---------------- */

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Add New Habit",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            },
            text = {
                Column {

                    OutlinedTextField(
                        value = habitName,
                        onValueChange = { habitName = it },
                        label = { Text("Habit name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (habitName.isNotBlank()) {
                                habitViewModel.addHabit(habitName)
                                habitName = ""
                                showDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryBlue
                        )
                    ) {
                        Text("Add Habit", color = Color.White)
                    }
                }
            },
            confirmButton = {}
        )
    }
}

/* -------------------- 🧩 HABIT CARD -------------------- */

@Composable
fun HabitCard(
    habit: HabitEntity,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = habit.isCompleted,
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
                color = if (habit.isCompleted) TextSecondary else TextPrimary
            )
        }
    }
}
