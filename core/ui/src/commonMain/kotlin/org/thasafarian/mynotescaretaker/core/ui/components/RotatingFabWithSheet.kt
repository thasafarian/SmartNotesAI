package org.thasafarian.mynotescaretaker.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import org.thasafarian.mynotescaretaker.core.util.isWideLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RotatingFabWithSheet(
    showBottomSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    bottomSheetState: SheetState,
    modifier: Modifier,
    scope: CoroutineScope,
    onAddTask: (String) -> Unit,
    onEditTask: (Task) -> Unit,
    editingTask: Task?,
    newTaskTitle: String,
    onTitleChange: (String) -> Unit,
    onClearEditingTask: () -> Unit,
    ) {
    var shouldRotate by remember { mutableStateOf(false) }

    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (shouldRotate) 60f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "fabRotation"
    )

    // Icon transition (Add ↔ Close)
    val currentIcon = if (shouldRotate) Icons.Default.Close else Icons.Default.Add

    // When rotation completes to 60 degrees, show bottom sheet
    LaunchedEffect(rotation) {
        if (rotation == 60f && !showBottomSheet) {
            onShowSheetChange(true)
            onTitleChange("")
        }
    }

    // Auto rotate back when sheet closes
    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) {
            shouldRotate = false
        }
    }

    if (isWideLayout()) {
        FloatingActionButton(
            onClick = {
                if (!showBottomSheet) {
                    shouldRotate = true
                    onTitleChange("")
                    onClearEditingTask()
                } else {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        onShowSheetChange(false)
                    }
                }
            },
            modifier = modifier
                .padding(24.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            Icon(currentIcon, contentDescription = "Add or Close")
        }
    }

    // --- Bottom Sheet for adding/editing tasks ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onShowSheetChange(false) },
            sheetState = bottomSheetState
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Task title") },
                    modifier = Modifier.fillMaxWidth()
                        .height(100.dp)
                )
                Spacer(Modifier.height(8.dp))
                PrioritySelectorRow(
                    onPrioritySelected = {

                    }
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            if (editingTask != null) {
                                val updatedTask = editingTask.copy(title = newTaskTitle)
                                onEditTask(updatedTask)
                            } else {
                                onAddTask(newTaskTitle)
                            }
                            onTitleChange("")
                            scope.launch { bottomSheetState.hide() }
                                .invokeOnCompletion { onShowSheetChange(false) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save Task")
                }
                Spacer(Modifier.height(16.dp))



            }
        }
    }
}

