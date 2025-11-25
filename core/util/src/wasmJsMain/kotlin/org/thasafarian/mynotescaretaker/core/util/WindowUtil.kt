package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.*
import kotlinx.browser.window

@Composable
actual fun rememberWindowType(): WindowType {
    var width by remember { mutableStateOf(window.innerWidth) }

    // Listen for browser window resize
    DisposableEffect(Unit) {
        val listener: (org.w3c.dom.events.Event) -> Unit = {
            width = window.innerWidth
        }
        window.addEventListener("resize", listener)
        onDispose { window.removeEventListener("resize", listener) }
    }

    return when {
        width < 600 -> WindowType.Compact
        width < 840 -> WindowType.Medium
        else -> WindowType.Expanded
    }
}

