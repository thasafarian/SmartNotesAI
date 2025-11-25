package org.thasafarian.mynotescaretaker.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val status: Int,
    val createdAt: String,
    val aiResponse: String? = null
)

