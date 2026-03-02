package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.habitcoachai.ui.components.WeeklyLineChartView
import com.example.habitcoachai.viewmodel.HabitViewModel

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

    val stats by habitViewModel
        .weeklyStatsPerHabit()
        .collectAsStateWithLifecycle(emptyMap())

    val completionRate by habitViewModel
        .completionRateLast30Days()
        .collectAsStateWithLifecycle(0)

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

            // ⭐ STREAK CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Current Streak", fontSize = 20.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "$streak days",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBlue
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (streak == 0)
                            "Start completing habits today!"
                        else
                            "Keep going, you're doing great!",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Consistency Calendar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            Spacer(modifier = Modifier.height(16.dp))
            WeekHeaderRow()
            Spacer(modifier = Modifier.height(6.dp))
            CalendarGrid(habitViewModel)

            Spacer(modifier = Modifier.height(32.dp))

            Text("Weekly Progress", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if(stats.isEmpty()){
                        Text(
                            text = "Start completing habits to see weekly trends 📊",
                            color = TextSecondary
                        )
                    } else {
                        WeeklyLineChartView(stats)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Insights", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            AnalyticsSection(habitViewModel)

            Text(
                text = when {
                    completionRate >= 80 -> "🔥 You're extremely consistent!"
                    completionRate >= 50 -> "👍 You're building momentum!"
                    completionRate > 0 -> "Start small. Consistency beats intensity."
                    else -> "Your journey starts today 💙"
                },
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun CalendarGrid(habitViewModel: HabitViewModel) {
    val completedSet by habitViewModel
        .completedDatesAsLocalDate()
        .collectAsStateWithLifecycle(emptySet())

    val today = LocalDate.now()
    val firstDayOfMonth = today.withDayOfMonth(1)
    val daysInMonth = today.lengthOfMonth()

    // Monday = 1 ... Sunday = 7
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

    val calendarDays = buildList<LocalDate?> {
        // 🔹 empty cells before month starts
        repeat(startOffset) { add(null) }

        // 🔹 actual month days
        for (day in 1..daysInMonth) {
            add(firstDayOfMonth.withDayOfMonth(day))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        calendarDays.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                week.forEach { date ->
                    if (date == null) {
                        Spacer(modifier = Modifier.size(40.dp))
                    } else {
                        DayBox(date, date in completedSet)
                    }
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
    val today = LocalDate.now()
    val border = if(date == today) BorderStroke(2.dp, SecondaryBlue) else null

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCompleted) SecondaryBlue else CardDark)
            .then(if (border != null) Modifier.border(border,RoundedCornerShape(8.dp)) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(date.dayOfMonth.toString(), color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun AnalyticsSection(habitViewModel: HabitViewModel){
    val completionRate by habitViewModel.completionRateLast30Days().collectAsStateWithLifecycle(0)
    val bestDay by habitViewModel.bestDayOfWeek().collectAsStateWithLifecycle("N/A")
    val total by habitViewModel.totalCompletions().collectAsStateWithLifecycle(0)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard("Completion Rate", "$completionRate%", Modifier.fillMaxWidth())
        StatCard("Best Day", bestDay, Modifier.fillMaxWidth())
        StatCard("Total", "$total", Modifier.fillMaxWidth())
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ){
        Column(
            modifier = Modifier.fillMaxWidth().height(110.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(title, fontSize = 18.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 28.sp)
        }
    }
}

@Composable
fun WeekHeaderRow() {
    val days = listOf("Mo","Tu","We","Th","Fr","Sa","Su")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        days.forEach {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Text(it, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
