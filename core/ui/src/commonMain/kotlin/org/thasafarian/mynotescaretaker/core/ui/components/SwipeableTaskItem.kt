package org.thasafarian.mynotescaretaker.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.thasafarian.mynotescaretaker.core.domain.model.Task


@Composable
fun SwipeableTaskItem(
    modifier: Modifier,
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    // animate offset for smooth swipe
    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp, label = "swipeAnim")

    val maxSwipe = -160f // reveal width for 2 buttons (80dp each)

    Box(
        modifier = modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // --- Background buttons (behind card) ---
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.medium),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit button
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF81C784)), // Soft green
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onEdit(task) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )
                }
            }

            // Delete button
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFE57373)), // Soft red
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onDelete(task) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }

        // --- Foreground card (swipeable) ---
        Card(
            modifier = Modifier
                .offset(x = animatedOffsetX)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // snap open or close
                            scope.launch {
                                offsetX = if (offsetX < maxSwipe / 2) maxSwipe else 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = (offsetX + dragAmount).coerceIn(maxSwipe, 0f)
                            offsetX = newOffset
                        }
                    )
                }
                .clickable { onClick(task) },
            elevation = CardDefaults.cardElevation(4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f) ) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Status: ${task.status}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Created: ${task.createdAt.take(10)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.DragHandle,
                    contentDescription = "DragIndicator",
                )
            }
        }
    }
}

