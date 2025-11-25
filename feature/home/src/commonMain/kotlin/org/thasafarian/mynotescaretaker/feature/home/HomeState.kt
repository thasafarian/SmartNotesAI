package org.thasafarian.mynotescaretaker.feature.home

import org.thasafarian.mynotescaretaker.core.domain.model.Task

data class HomeState(
    var tasks: List<Task> = emptyList(),
    var groupedTasks: Map<String, List<Task>> = emptyMap(),
    var aiSuggestionTasks: List<Pair<String, Map<String, List<Task>>>>? = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val taskToDelete: Task? = null,
    val aiResponse: String? = null
)

