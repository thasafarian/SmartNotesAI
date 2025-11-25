package org.thasafarian.mynotescaretaker.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.thasafarian.mynotescaretaker.feature.home.components.AIAssistantTransformingButton
import org.thasafarian.mynotescaretaker.feature.home.components.AiSuggestionList

@Composable
fun HomeScreen(
    isGrid: Boolean,
    openNewTask: Boolean,
    onDismissNewTask: (Boolean) -> Unit
) {
    val viewModel = remember { HomeViewModel() }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    var showAiSuggestionsTasks by remember { mutableStateOf(false) }

    LaunchedEffect(Unit ) {
        scope.launch {
            viewModel.loadGroupedTasks()
        }
    }

    LaunchedEffect(openNewTask, state.aiSuggestionTasks) {
        val hasSuggestions = !state.aiSuggestionTasks.isNullOrEmpty()

        showAiSuggestionsTasks = when {
            openNewTask -> false
            hasSuggestions -> true
            else -> false
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        // === HomeContent (main UI) ===
        Box(modifier = Modifier.padding(top = if (!showAiSuggestionsTasks) 16.dp else 32.dp)) {
            AnimatedVisibility(
                visible = !showAiSuggestionsTasks,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // slide up from bottom
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it }, // slide up and away
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            ) {
                HomeContent(
                    state = state,
                    isGrid = isGrid,
                    openNewTask = openNewTask,
                    onDismissNewTask = { value -> onDismissNewTask(value) },
                    onDeleteConfirm = { viewModel.removeTask(it) },
                    onDismissDialog = { viewModel.dismissDialog() },
                    onDeleteRequest = { viewModel.confirmDelete(it) },
                    moveTask = { from, to -> viewModel.moveTask(from, to) },
                    onReorderAcrossGroups = { task, label ->
                        viewModel.moveGroupedTask(
                            task,
                            label
                        )
                    },
                    onReorderWithinGroup = { label, tasks ->
                        viewModel.reorderWithinGroup(
                            label,
                            tasks
                        )
                    },
                    onAddTask = { title -> viewModel.addGroupedTask(title) },
                    onEditTask = { task -> viewModel.editTask(task) }
                )
            }


            AnimatedVisibility(
                visible = showAiSuggestionsTasks, // hidden when AI list appears
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // slide up from bottom
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it }, // slide up and away
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            ) {
                AiSuggestionList(
                    state = state,
                    onTaskClick = {

                    }
                )
            }
        }

        // floating top-right button
        AIAssistantTransformingButton(
            onClick = { userPrompt ->
                viewModel.getAiSuggestionText(
                    userPrompt = userPrompt
                )
            }
        )
    }
}

