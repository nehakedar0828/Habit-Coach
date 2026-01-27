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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitcoachai.data.local.db.HabitDatabase
import com.example.habitcoachai.viewmodel.HabitViewModel
import com.example.habitcoachai.viewmodel.HabitViewModelFactory

@Composable
fun DashboardScreen(userName: String?) {

    // 🔵 Blue gradient background
    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color.White
        )
    )

    var showDialog by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }


    // 🗄️ Database + ViewModel
    val context = LocalContext.current
    val database = remember { HabitDatabase.getDatabase(context) }

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(database)
    )

    val habits by habitViewModel.habits.collectAsState(initial = emptyList())

    // 👋 Greeting text (safe + defensive)
    val greeting = if (userName.isNullOrBlank()) {
        "Welcome !👋"
    } else {
        "Welcome, $userName !👋"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blueGradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 32.dp)
        ) {

            // 👋 Greeting
            Text(
                text = greeting,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Your current habits : 💪🥳 ",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(12.dp))


            // 📋 Habit List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(
                        habit = habit,
                        onCheckedChange = { checked ->
                            habitViewModel.toggleHabitCompletion(
                                habit.id,
                                checked
                            )
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = Color(0xFF1565C0),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Text(
                text = "+",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Add New Habit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = habitName,
                        onValueChange = { habitName = it },
                        label = { Text("Habit Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if(habitName.isNotBlank()){
                                habitViewModel.addHabit(habitName)
                                habitName = ""
                                showDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0)
                        )
                    ) {
                        Text("Add Habit",color = Color.White)
                    }
                }
            },
            confirmButton = {}
        )
    }

}

@Composable
fun HabitCard(
    habit: com.example.habitcoachai.data.local.entity.HabitEntity,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(26.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1565C0),
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = habit.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if(habit.isCompleted)
                    Color(0xFF90A4AE)
                    else
                    Color(0xFF1565C0)
            )
        }
    }
}
