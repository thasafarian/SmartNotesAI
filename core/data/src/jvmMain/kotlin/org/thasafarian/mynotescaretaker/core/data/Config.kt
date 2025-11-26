package org.thasafarian.mynotescaretaker.core.data

/**
 * JVM implementation of Config.
 * Reads from environment variables.
 */
actual object Config {
    private fun getEnv(key: String, defaultValue: String): String {
        return System.getenv(key) ?: defaultValue
    }

    actual val baseUrl: String
        get() = getEnv(
            "API_BASE_URL",
            "https://6902f0f3d0f10a340b21f140.mockapi.io/api/v1/todo"
        )

    actual val geminiApiKey: String
        get() = getEnv("GEMINI_API_KEY", "")

    actual val geminiBaseUrl: String
        get() = getEnv(
            "GEMINI_BASE_URL",
            "https://generativelanguage.googleapis.com/v1beta/models"
        )

    actual val geminiModel: String
        get() = getEnv("GEMINI_MODEL", "gemini-2.5-flash-lite:generateContent")
}

