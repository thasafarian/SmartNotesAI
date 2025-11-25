package org.thasafarian.mynotescaretaker

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.thasafarian.mynotescaretaker.ui.MainScreen

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "TodoMultiPlatform",
    ) {
        MainScreen()
    }
}