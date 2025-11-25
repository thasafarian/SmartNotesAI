package org.thasafarian.mynotescaretaker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrioritySelectorRow(
    modifier: Modifier = Modifier,
    onPrioritySelected: (String) -> Unit = {}
) {
    var selectedPriority by remember { mutableStateOf("Low") }

    val priorities = listOf("Low", "Medium", "High")
    val colors = mapOf(
        "Low" to Color(0xFF4CAF50),     // Green
        "Medium" to Color(0xFFFFC107),  // Amber
        "High" to Color(0xFFF44336)     // Red
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        priorities.forEach { priority ->
            val isSelected = selectedPriority == priority
            val bgColor = if (isSelected) colors[priority]!!.copy(alpha = 0.2f) else Color.Transparent
            val borderColor = colors[priority]!!
            val textColor = if (isSelected) colors[priority]!! else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable {
                        selectedPriority = priority
                        onPrioritySelected(priority)
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = priority,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

