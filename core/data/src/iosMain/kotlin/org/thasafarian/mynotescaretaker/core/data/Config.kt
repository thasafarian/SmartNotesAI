package org.thasafarian.mynotescaretaker.core.data

import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of Config.
 * Reads from Info.plist or environment variables.
 * For production, use environment variables or secure keychain storage.
 */
actual object Config {
    private fun getValue(key: String, defaultValue: String): String {
        // Try to read from Info.plist first
        val bundleValue = NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String
        if (!bundleValue.isNullOrEmpty()) {
            return bundleValue
        }
        
        // Fallback to environment variable
        // Note: On iOS, environment variables need to be set in Xcode scheme
        return defaultValue
    }

    actual val baseUrl: String
        get() = getValue(
            "API_BASE_URL",
            "https://6902f0f3d0f10a340b21f140.mockapi.io/api/v1/todo"
        )

    actual val geminiApiKey: String
        get() = getValue("GEMINI_API_KEY", "")

    actual val geminiBaseUrl: String
        get() = getValue(
            "GEMINI_BASE_URL",
            "https://generativelanguage.googleapis.com/v1beta/models"
        )

    actual val geminiModel: String
        get() = getValue("GEMINI_MODEL", "gemini-2.5-flash-lite:generateContent")
}

