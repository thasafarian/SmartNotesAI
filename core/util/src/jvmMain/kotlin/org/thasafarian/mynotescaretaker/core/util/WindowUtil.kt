package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberWindowType(): WindowType {
    var windowType = WindowType.Compact
    BoxWithConstraints {
        val width = maxWidth
        windowType = when {
            width < 600.dp -> WindowType.Compact
            width < 840.dp ->WindowType.Medium
            else -> WindowType.Expanded
        }
    }
    return windowType
}

