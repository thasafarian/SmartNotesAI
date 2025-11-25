package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.Composable

enum class WindowType { Compact, Medium, Expanded }

/**
 * Multiplatform window size detector.
 */
@Composable
expect fun rememberWindowType(): WindowType


