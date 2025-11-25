package org.thasafarian.mynotescaretaker.core.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedbac  k.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import org.thasafarian.mynotescaretaker.core.domain.model.TaskListItem
import sh.calvin.reorderable.*
import kotlin.collections.get

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun SharedTransitionScope.TaskList(
    groupedTasks: Map<String, List<Task>>,
    lazyListState: LazyListState,
    hapticFeedback: HapticFeedback,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onReorderAcrossGroups: (Task, String) -> Unit,
    onReorderWithinGroup: (String, List<Task>) -> Unit
) {
    // --- Flatten the grouped data into a single list ---
    val flatList by derivedStateOf {
        groupedTasks
            .filter { (_, tasks) -> tasks.isNotEmpty() } // skip header if it has empty sublist
            .flatMap { (label, tasks) ->
            listOf(TaskListItem.Header(label)) +
                    tasks.map { TaskListItem.TaskItem(it, label) }
        }
    }


    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromItem = flatList.getOrNull(from.index)
        val toItem = flatList.getOrNull(to.index)

        if (fromItem == null || toItem == null) return@rememberReorderableLazyListState

        when {
            fromItem is TaskListItem.TaskItem && toItem is TaskListItem.TaskItem -> {
                if (fromItem.group == toItem.group) {
                    // same group reorder
                    val currentGroup = fromItem.group
                    val tasks = groupedTasks[currentGroup]?.toMutableList() ?: mutableListOf()

                    val fromIndex = tasks.indexOfFirst { it.id == fromItem.task.id }
                    val toIndex = tasks.indexOfFirst { it.id == toItem.task.id }

                    if (fromIndex != -1 && toIndex != -1) {
                        tasks.add(toIndex, tasks.removeAt(fromIndex))
                        onReorderWithinGroup(currentGroup, tasks)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                } else {
                    // moved across group header
                    onReorderAcrossGroups(fromItem.task, toItem.group)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }

            fromItem is TaskListItem.TaskItem && toItem is TaskListItem.Header -> {
                // moved directly onto header
                onReorderAcrossGroups(fromItem.task, toItem.label)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        flatList.forEachIndexed { index, item ->
            when (item) {
                is TaskListItem.Header -> {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                is TaskListItem.TaskItem -> {
                    item(key = item.task.id) {
                        ReorderableItem(
                            reorderableLazyColumnState,
                            key = item.task.id
                        ) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                            val modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = item.task.id),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragStopped = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    }
                                )

                            Surface(shadowElevation = elevation) {
                                SwipeableTaskItem(
                                    modifier = modifier,
                                    task = item.task,
                                    onEdit = onEdit,
                                    onDelete = onDelete
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TaskGrid(
    groupedTasks: Map<String, List<Task>>,
    lazyGridState: LazyGridState,
    hapticFeedback: HapticFeedback,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onReorderAcrossGroups: (Task, String) -> Unit,
    onReorderWithinGroup: (String, List<Task>) -> Unit
) {
    val flatList by remember(groupedTasks) {
        derivedStateOf {
            groupedTasks
                .filter { (_, tasks) -> tasks.isNotEmpty() }
                .flatMap { (label, tasks) ->
                    listOf(TaskListItem.Header(label)) +
                            tasks.map { TaskListItem.TaskItem(it, label) }
                }
        }
    }

    val reorderableGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        val fromItem = flatList.getOrNull(from.index)
        val toItem = flatList.getOrNull(to.index)
        if (fromItem == null || toItem == null) return@rememberReorderableLazyGridState

        when {
            fromItem is TaskListItem.TaskItem && toItem is TaskListItem.TaskItem -> {
                if (fromItem.group == toItem.group) {
                    val currentGroup = fromItem.group
                    val tasks = groupedTasks[currentGroup]?.toMutableList() ?: mutableListOf()

                    val fromIndex = tasks.indexOfFirst { it.id == fromItem.task.id }
                    val toIndex = tasks.indexOfFirst { it.id == toItem.task.id }

                    if (fromIndex != -1 && toIndex != -1) {
                        tasks.add(toIndex, tasks.removeAt(fromIndex))
                        onReorderWithinGroup(currentGroup, tasks)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                } else {
                    onReorderAcrossGroups(fromItem.task, toItem.group)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }

            fromItem is TaskListItem.TaskItem && toItem is TaskListItem.Header -> {
                onReorderAcrossGroups(fromItem.task, toItem.label)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        flatList.forEachIndexed { index, item ->
            when (item) {
                is TaskListItem.Header -> {
                    item(
                        key = "header_${item.label}",
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                is TaskListItem.TaskItem -> {
                    item(key = item.task.id) {
                        ReorderableItem(
                            reorderableGridState,
                            key = item.task.id
                        ) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                            Surface(
                                shadowElevation = elevation,
                                tonalElevation = if (isDragging) 6.dp else 1.dp,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = item.task.id),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .longPressDraggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        }
                                    )
                            ) {
                                SwipeableTaskItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    task = item.task,
                                    onEdit = onEdit,
                                    onDelete = onDelete
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ClickableTaskItem(
    modifier: Modifier,
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(task) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("Status: ${task.status}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Created: ${task.createdAt.take(10)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ⋮ Dropdown menu (3 dots)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onEdit(task)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onDelete(task)
                        }
                    )
                }
            }
        }
    }
}

