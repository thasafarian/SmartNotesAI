package org.thasafarian.mynotescaretaker.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import org.thasafarian.mynotescaretaker.core.domain.model.TaskListItem
import org.thasafarian.mynotescaretaker.core.ui.components.RotatingFabWithSheet
import org.thasafarian.mynotescaretaker.core.ui.components.TaskGrid
import org.thasafarian.mynotescaretaker.core.ui.components.TaskList
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun HomeContent(
    state: HomeState,
    isGrid: Boolean,
    openNewTask: Boolean,
    onDismissNewTask: (Boolean) -> Unit,
    onDeleteConfirm: (Task) -> Unit,
    onDismissDialog: () -> Unit,
    onDeleteRequest: (Task) -> Unit,
    moveTask: (from: Int, to: Int) -> Unit,
    onReorderAcrossGroups: (Task, String) -> Unit,
    onReorderWithinGroup: (String, List<Task>) -> Unit,
    onEditTask: (Task) -> Unit,
    onAddTask: (String) -> Unit,
) {

    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()


    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(openNewTask) {
        showBottomSheet = openNewTask
    }
    when {
        state.isLoading -> Box(
            Modifier.fillMaxSize(),
            Alignment.Center
        ) { CircularProgressIndicator() }

        state.errorMessage != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Error: ${state.errorMessage}")
        }

        else -> {
            Box(Modifier.fillMaxSize().padding(16.dp)) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = isGrid,
                        transitionSpec = {
                            (fadeIn(tween(300)) + scaleIn(tween(300))) togetherWith
                                    (fadeOut(tween(300)) + scaleOut(tween(300)))
                        },
                        label = "GridListTransition"
                    ) { showGrid ->
                        if (showGrid) {
                            TaskGrid(
                                groupedTasks = state.groupedTasks,
                                lazyGridState = lazyGridState,
                                hapticFeedback = hapticFeedback,
                                animatedVisibilityScope = this@AnimatedContent,
                                onEdit = { task ->
                                    editingTask = task
                                    newTaskTitle = task.title
                                    showBottomSheet = true
                                },
                                {
                                    onDeleteRequest(it)
                                },
                                onReorderAcrossGroups = onReorderAcrossGroups,
                                onReorderWithinGroup = onReorderWithinGroup
                            )
                        } else {
                            Column {
                                Text(text = state.aiResponse ?: "")
                                TaskList(
                                    groupedTasks = state.groupedTasks,
                                    lazyListState = lazyListState,
                                    hapticFeedback = hapticFeedback,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    onEdit = { task ->
                                        editingTask = task
                                        newTaskTitle = task.title
                                        showBottomSheet = true
                                    },
                                    {
                                        onDeleteRequest(it)
                                    },
                                    onReorderAcrossGroups = onReorderAcrossGroups,
                                    onReorderWithinGroup = onReorderWithinGroup
                                )
                            }
                        }
                    }
                }

                // --- Bottom Sheet ---
                RotatingFabWithSheet(
                    showBottomSheet = showBottomSheet,
                    onShowSheetChange = {
                        showBottomSheet = it
                        onDismissNewTask(it)
                    },
                    bottomSheetState = bottomSheetState,
                    Modifier.align(Alignment.BottomEnd),
                    scope = scope,
                    onAddTask = onAddTask,
                    onEditTask = onEditTask,
                    editingTask = editingTask,
                    newTaskTitle = newTaskTitle,
                    onTitleChange = {
                        newTaskTitle = it
                    },
                    onClearEditingTask = {
                        editingTask = null
                    }
                )

            }
        }
    }


    state.taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${task.title}'?") },
            confirmButton = {
                TextButton(onClick = { onDeleteConfirm(task) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) { Text("Cancel") }
            }
        )
    }
}

