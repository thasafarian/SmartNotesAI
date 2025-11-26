package org.thasafarian.mynotescaretaker.core.data

/**
 * Configuration interface for API credentials and URLs.
 * Implementations should read from secure sources (environment variables, local properties, etc.)
 */
expect object Config {
    val baseUrl: String
    val geminiApiKey: String
    val geminiBaseUrl: String
    val geminiModel: String
}

