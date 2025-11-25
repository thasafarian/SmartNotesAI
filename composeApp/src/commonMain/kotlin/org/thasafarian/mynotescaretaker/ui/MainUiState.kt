package org.thasafarian.mynotescaretaker.ui

data class MainUiState(
    val selectedRoute: String = "home",
    val isWideLayout: Boolean = false,
    val isGrid: Boolean = false,
    val openNewTask: Boolean = false,
    val isDarkTheme: Boolean = false
)
