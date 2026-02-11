package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitcoachai.ui.components.WeeklyBarChartView
import com.example.habitcoachai.viewmodel.HabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate

private val BackgroundDark = Color(0xFF0E141B)
private val CardDark = Color(0xFF1B2330)
private val SecondaryBlue = Color(0xFF38BDF8)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextSecondary = Color(0xFF94A3B8)

@Composable
fun StreakScreen(
    habitViewModel: HabitViewModel,
    onBack: () -> Unit
) {

    LaunchedEffect(Unit){
        habitViewModel.loadCurrentStreak()
        habitViewModel.refreshToday()
    }

    val streak by habitViewModel.currentStreak

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Progress 📈",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardDark
                )
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Current Streak ",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$streak days",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (streak == 0)
                        "Start completing habits today!"
                        else
                        "Keep going, you're doing great!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Consistency Calendar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalendarGrid(habitViewModel)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weekly Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WeeklyGraphSection(habitViewModel)
                }
            }

        }
    }
}

@Composable
fun CalendarGrid(habitViewModel: HabitViewModel){

    val completedSet by habitViewModel
        .completedDatesAsLocalDate()
        .collectAsStateWithLifecycle(emptySet())


    val today = LocalDate.now()

    val days = remember(today) {
        (0..29)
            .map {
            today.minusDays(it.toLong())
            }
            .reversed()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.chunked(7).forEach { week ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { date ->
                    DayBox(date, date in completedSet)
                }

                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

        }
    }

}

@Composable
fun DayBox(date: LocalDate, isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCompleted) SecondaryBlue else CardDark),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text = date.dayOfMonth.toString(),
            color = TextPrimary,
            fontSize = 14.sp
        )
    }
}


@Composable
fun WeeklyGraphSection(habitViewModel: HabitViewModel){

    val stats by habitViewModel
        .weeklyCompletionStats()
        .collectAsStateWithLifecycle(emptyMap())

    WeeklyBarChartView(stats)
}