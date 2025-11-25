package org.thasafarian.mynotescaretaker.core.domain.model

sealed class TaskListItem {
    data class Header(val label: String) : TaskListItem()
    data class TaskItem(val task: Task, val group: String) : TaskListItem()
}

