package org.thasafarian.mynotescaretaker.core.data.model

import kotlinx.serialization.Serializable
import org.thasafarian.mynotescaretaker.core.domain.model.Task

@Serializable
data class PostTaskRequest(
    val requestId: String,
    val items: Task,
    val count: Int,
    val anyKey: String = "anyValue"
)

