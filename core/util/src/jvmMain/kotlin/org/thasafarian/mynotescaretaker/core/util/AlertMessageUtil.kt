package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.Composable
import javax.swing.JOptionPane


@Composable
actual fun showAlert(message: String) {
    JOptionPane.showMessageDialog(null, message)
}

