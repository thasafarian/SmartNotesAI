package org.thasafarian.mynotescaretaker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var uiState by mutableStateOf(
        MainUiState(
            selectedRoute = "home",
            isWideLayout = false,
            isGrid = false
        )
    )
        private set

    fun onRouteSelected(route: String) {
        uiState = uiState.copy(selectedRoute = route)
    }

    fun openNewTask(value: Boolean) {
        uiState = uiState.copy(openNewTask = value )
    }

    fun toggleGrid() {
        uiState = uiState.copy(isGrid = !uiState.isGrid)
    }

    fun updateLayout(isWide: Boolean) {
        uiState = uiState.copy(isWideLayout = isWide)
    }

    fun toggleTheme() {
        uiState = uiState.copy(isDarkTheme = !uiState.isDarkTheme)
    }
}
