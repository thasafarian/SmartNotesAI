package org.thasafarian.mynotescaretaker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.thasafarian.mynotescaretaker.core.di.appModule
import org.thasafarian.mynotescaretaker.di.appModule as composeAppModule
import org.thasafarian.mynotescaretaker.ui.components.AdaptiveNavigationBar
import org.thasafarian.mynotescaretaker.ui.components.MyBottomBar
import org.thasafarian.mynotescaretaker.feature.settings.ProfileScreen
import org.thasafarian.mynotescaretaker.feature.tasks.TaskScreen
import org.thasafarian.mynotescaretaker.feature.home.HomeScreen
import org.thasafarian.mynotescaretaker.core.util.isWideLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    platformModule: Module = Module()
) {
    KoinApplication(
        application = {
            modules(appModule, composeAppModule, platformModule)
        }
    ) {
        val viewModel: MainViewModel = koinViewModel()
        val state = viewModel.uiState
        val isWide = isWideLayout()
        LaunchedEffect(isWide) {
            viewModel.updateLayout(isWide)
        }

        val colorScheme = if (state.isDarkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }

        MaterialTheme(colorScheme = colorScheme) {
            if (state.isWideLayout) {
                WideLayout(state, viewModel)
            } else {
                CompactLayout(state, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WideLayout(state: MainUiState, viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(Modifier.fillMaxSize()) {
            AdaptiveNavigationBar(
                selectedRoute = state.selectedRoute,
                onItemSelected = { viewModel.onRouteSelected(it) },
                modifier = Modifier.fillMaxHeight()
            )

            HorizontalDivider(
                Modifier.fillMaxHeight().width(1.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Todo List") },
                        actions = {
                            IconButton(onClick = { viewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (state.isDarkTheme)
                                        Icons.Default.LightMode
                                    else
                                        Icons.Default.DarkMode,
                                    contentDescription = if (state.isDarkTheme)
                                        "Switch to Light Mode"
                                    else
                                        "Switch to Dark Mode"
                                )
                            }

                            if (state.selectedRoute == "home") {
                                IconButton(onClick = { viewModel.toggleGrid() }) {
                                    Icon(
                                        imageVector = if (state.isGrid)
                                            Icons.AutoMirrored.Filled.List
                                        else
                                            Icons.Default.GridView,
                                        contentDescription = if (state.isGrid)
                                            "Switch to List View"
                                        else
                                            "Switch to Grid View"
                                    )
                                }
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(padding)
                ) {
                    when (state.selectedRoute) {
                        "home" -> HomeScreen(
                            isGrid = state.isGrid,
                            openNewTask = false,
                            onDismissNewTask = {}
                        )

                        "tasks" -> TaskScreen()
                        "settings" -> ProfileScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactLayout(state: MainUiState, viewModel: MainViewModel) {

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                MyBottomBar(
                    selectedRoute = state.selectedRoute,
                    onAddClick = { viewModel.openNewTask(true) },
                    onItemSelected = { viewModel.onRouteSelected(it) }
                )
            },
            topBar = {
                TopAppBar(
                    title = { Text("Todo List") },
                    actions = {
                        // 🌗 Theme toggle for mobile
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (state.isDarkTheme)
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,
                                contentDescription = if (state.isDarkTheme)
                                    "Switch to Light Mode"
                                else
                                    "Switch to Dark Mode"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(innerPadding)
            ) {
                when (state.selectedRoute) {
                    "home" -> HomeScreen(
                        isGrid = state.isGrid,
                        openNewTask = state.openNewTask,
                        onDismissNewTask = { value -> viewModel.openNewTask(value) }

                    )

                    "tasks" -> TaskScreen()
                    "settings" -> ProfileScreen()
                }
            }
        }
    }
}
