package org.thasafarian.mynotescaretaker.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiTaskSuggestion(
    val suggestion: String,
    val tasks: List<Task>
)

