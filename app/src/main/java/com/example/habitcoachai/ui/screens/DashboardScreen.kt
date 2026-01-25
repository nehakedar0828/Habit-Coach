package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(userName : String) {

    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color.White
        )
    )

    val greeting = if(userName.isNullOrEmpty()){
        "Welcome"
    }else{
        "Welcome, $userName"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blueGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = greeting,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        )

    }
}