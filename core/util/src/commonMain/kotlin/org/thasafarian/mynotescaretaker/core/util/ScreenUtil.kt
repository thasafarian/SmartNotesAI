package org.thasafarian.mynotescaretaker.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun isWideLayout(): Boolean {
    val windowType = rememberWindowType()
    val isWideLayout = PlatformUtils.isDesktop || windowType != WindowType.Compact
    return isWideLayout
}


@Composable
fun getScreenSize(): Pair<Dp, Dp> {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    return if (windowInfo != null) {
        with(density) {
            Pair(
                windowInfo.containerSize.width.toDp(),
                windowInfo.containerSize.height.toDp()
            )
        }
    } else {
        Pair(360.dp, 640.dp) // fallback for desktop/web
    }
}

