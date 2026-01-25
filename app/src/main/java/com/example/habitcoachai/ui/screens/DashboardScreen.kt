package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

            Spacer(modifier = Modifier.height(20.dp))

            // 📋 Habit List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(habit.name)
                }
            }
        }
    }
}

@Composable
fun HabitCard(name: String) {
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
        Text(
            text = name,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1565C0)
        )
    }
}
