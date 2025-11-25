package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.Composable
import kotlinx.browser.window

@Composable
actual fun showAlert(message: String) {
    window.alert(message)
}

