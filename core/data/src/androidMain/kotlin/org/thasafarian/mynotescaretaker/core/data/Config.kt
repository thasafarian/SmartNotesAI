package org.thasafarian.mynotescaretaker.core.data

import java.util.Properties
import java.io.File

/**
 * Android implementation of Config.
 * Reads from local.properties file (which should be in .gitignore)
 * Falls back to environment variables if local.properties is not available.
 * 
 * To set credentials, add them to local.properties in the project root:
 * API_BASE_URL=https://your-api-url.com
 * GEMINI_API_KEY=your-api-key
 * GEMINI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/models
 * GEMINI_MODEL=gemini-2.5-flash-lite:generateContent
 */
actual object Config {
    private val properties: Properties by lazy {
        val props = Properties()
        // Try to find local.properties in project root (go up from module to project root)
        val projectRoot = File(System.getProperty("user.dir"))
        val localPropertiesFile = File(projectRoot, "local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { props.load(it) }
        }
        props
    }

    private fun getProperty(key: String, defaultValue: String): String {
        return properties.getProperty(key) 
            ?: System.getenv(key) 
            ?: defaultValue
    }

    actual val baseUrl: String
        get() = getProperty(
            "API_BASE_URL",
            "https://6902f0f3d0f10a340b21f140.mockapi.io/api/v1/todo"
        )

    actual val geminiApiKey: String
        get() = getProperty(
            "GEMINI_API_KEY",
            ""
        )

    actual val geminiBaseUrl: String
        get() = getProperty(
            "GEMINI_BASE_URL",
            "https://generativelanguage.googleapis.com/v1beta/models"
        )

    actual val geminiModel: String
        get() = getProperty(
            "GEMINI_MODEL",
            "gemini-2.5-flash-lite:generateContent"
        )
}

