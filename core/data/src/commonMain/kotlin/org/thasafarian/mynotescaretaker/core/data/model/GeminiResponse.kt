package org.thasafarian.mynotescaretaker.core.data.model

@kotlinx.serialization.Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
)

@kotlinx.serialization.Serializable
data class Candidate(
    val content: Content
)

@kotlinx.serialization.Serializable
data class Content(
    val parts: List<Part>
)

@kotlinx.serialization.Serializable
data class Part(
    val text: String
)

