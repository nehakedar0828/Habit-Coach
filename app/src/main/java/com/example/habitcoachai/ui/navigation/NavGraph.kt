package com.example.habitcoachai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitcoachai.ui.screens.DashboardScreen
import com.example.habitcoachai.ui.screens.HomeScreen
import com.example.habitcoachai.ui.screens.OnboardingScreen


@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppNav.WELCOME
    ) {
        composable(AppNav.WELCOME) {
            HomeScreen(navController)
        }

        composable(AppNav.ONBOARDING){
            OnboardingScreen(navController)
        }

        composable(AppNav.DASHBOARD) {
            DashboardScreen()
        }

    }
}


