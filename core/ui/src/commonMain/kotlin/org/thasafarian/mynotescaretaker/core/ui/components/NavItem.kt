package org.thasafarian.mynotescaretaker.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("Tasks", Icons.AutoMirrored.Filled.Notes, "tasks"),
    NavItem("Settings", Icons.Default.Settings, "settings"),
)

