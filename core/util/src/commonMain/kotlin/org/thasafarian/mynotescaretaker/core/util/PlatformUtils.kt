@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.thasafarian.mynotescaretaker.core.util

expect object PlatformUtils {
    val isDesktop: Boolean
    val isAndroid: Boolean
    val isIOS: Boolean
    val isWeb: Boolean
}

