package org.thasafarian.mynotescaretaker.core.data.model

@kotlinx.serialization.Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@kotlinx.serialization.Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@kotlinx.serialization.Serializable
data class GeminiPart(
    val text: String
)


