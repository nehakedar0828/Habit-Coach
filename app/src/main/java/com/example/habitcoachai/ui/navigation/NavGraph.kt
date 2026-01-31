package com.example.habitcoachai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitcoachai.data.preferences.UserPreferences
import com.example.habitcoachai.ui.screens.DashboardScreen
import com.example.habitcoachai.ui.screens.HomeScreen
import com.example.habitcoachai.ui.screens.OnboardingScreen
import com.example.habitcoachai.ui.screens.StreakScreen
import com.example.habitcoachai.viewmodel.UserViewModel
import com.example.habitcoachai.viewmodel.viewModelFactory


@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val context = LocalContext.current

    val userPrefs = remember{ UserPreferences(context) }
    val userViewModel : UserViewModel = viewModel(
        factory = viewModelFactory(userPreferences = userPrefs)
    )

    val userName by userViewModel.userName.collectAsState()

    val startDestination = if (userName.isNullOrEmpty()){
        AppNav.WELCOME
    }else{
        AppNav.DASHBOARD
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppNav.WELCOME) {
            HomeScreen(navController)
        }

        composable(AppNav.ONBOARDING){
            OnboardingScreen(navController)
        }

        composable(AppNav.DASHBOARD) {
            DashboardScreen(userName = userName ?: "",navController = navController)
        }

        composable(AppNav.STREAK){
            StreakScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}


