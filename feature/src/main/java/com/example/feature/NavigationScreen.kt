package com.example.feature

sealed class NavigationScreen(val route: String) {
    object Home : NavigationScreen("home")
    object ScreenTwo : NavigationScreen("Screen 2")
    object ScreenThree : NavigationScreen("Screen 3")
}