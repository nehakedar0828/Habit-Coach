package com.example.habitcoachai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.example.habitcoachai.data.preferences.UserPreferences
import com.example.habitcoachai.ui.navigation.AppNav
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController) {

    val context = LocalContext.current
    val userPrefs = remember{ UserPreferences(context)}
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }

    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color.White
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blueGradient)
            .padding(32.dp)
    ){
    Column(
            modifier = Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "What should we call you?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "This helps us personalize your habit journey",
            fontSize = 15.sp,
            color = Color(0xFF546E7A)
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = {
                Text(text = "Enter your name")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    scope.launch {
                        userPrefs.saveUserName(name)
                        navController.navigate(AppNav.DASHBOARD) {
                            popUpTo(AppNav.WELCOME) {
                                inclusive = true
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0)

        ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
}
