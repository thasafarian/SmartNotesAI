package org.thasafarian.mynotescaretaker.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import org.thasafarian.mynotescaretaker.feature.home.HomeState

@Composable
fun AiSuggestionList(
    state: HomeState,
    onTaskClick: (Task) -> Unit = {}
) {
    val typedTexts = remember { mutableStateMapOf<Int, String>() }
    val hasPlayed = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            typedTexts.clear()
            hasPlayed.clear()
        }
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.aiSuggestionTasks!!.forEachIndexed { suggestionIndex, (fullSuggestionText, groupedTasks) ->

                    item(key = "suggestion_text_$suggestionIndex") {
                        // Launch typing animation only once per suggestion
                        LaunchedEffect(suggestionIndex) {
                            if (hasPlayed[suggestionIndex] == true) return@LaunchedEffect

                            var text = ""
                            for (i in fullSuggestionText.indices) {
                                text = fullSuggestionText.take(i + 1)
                                typedTexts[suggestionIndex] = text
                                delay(20)
                            }
                            hasPlayed[suggestionIndex] = true
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 3.dp,
                            shadowElevation = 8.dp,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        ) {
                            val typedText = typedTexts[suggestionIndex] ?: ""

                            // Inline icon + animated text
                            val annotatedString = buildAnnotatedString {
                                appendInlineContent("robotIcon", "[icon]")
                                append("  $typedText")
                            }

                            val inlineContent = mapOf(
                                "robotIcon" to InlineTextContent(
                                    Placeholder(
                                        width = 18.sp,
                                        height = 18.sp,
                                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.SmartToy,
                                        contentDescription = "AI",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                CircleShape
                                            )
                                            .padding(2.dp)
                                    )
                                }
                            )

                            Text(
                                text = annotatedString,
                                inlineContent = inlineContent,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.4.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .graphicsLayer {
                                        alpha = 0.95f
                                        scaleX = 1.01f
                                        scaleY = 1.01f
                                    }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    groupedTasks.forEach { (header, tasks) ->
                        if (tasks.isNotEmpty()) {
                            item(key = "header_${suggestionIndex}_$header") {
                                Text(
                                    text = header,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }

                            tasks.forEach { task ->
                                item(key = "task_${suggestionIndex}_${task.id}") {
                                    TaskRow(
                                        task = task,
                                        onClick = { onTaskClick(task) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    if (suggestionIndex < state.aiSuggestionTasks!!.lastIndex) {
                        item(key = "divider_$suggestionIndex") {
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskRow(task: Task, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


