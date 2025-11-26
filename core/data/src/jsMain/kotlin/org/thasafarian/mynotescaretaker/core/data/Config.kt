package org.thasafarian.mynotescaretaker.core.data

/**
 * JavaScript implementation of Config.
 * Reads from environment variables or window object.
 * For web builds, use build-time environment variables.
 */
actual object Config {
    private fun getEnv(key: String, defaultValue: String): String {
        // In JS, environment variables are typically injected at build time
        // For now, we'll use defaults. In production, use webpack/gradle to inject env vars
        return defaultValue
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

