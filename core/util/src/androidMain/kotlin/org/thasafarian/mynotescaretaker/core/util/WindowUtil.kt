package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberWindowType(): WindowType {
    var windowType by remember { mutableStateOf(WindowType.Compact) }

    BoxWithConstraints {
        val width = maxWidth
        LaunchedEffect(width) {
            windowType = when {
                width < 600.dp -> WindowType.Compact
                width < 840.dp -> WindowType.Medium
                else -> WindowType.Expanded
            }
        }
    }

    return windowType
}

