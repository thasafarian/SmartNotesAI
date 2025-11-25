package org.thasafarian.mynotescaretaker.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thasafarian.mynotescaretaker.core.data.TaskRepository
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import org.thasafarian.mynotescaretaker.core.util.MyDateTimeUtil
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val result = repository.fetchTasks()
                _state.value = _state.value.copy(tasks = result, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun loadGroupedTasks() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val result = repository.fetchTasksGrouped()
                _state.value = _state.value.copy(groupedTasks = result, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun addTask(title: String) {
        viewModelScope.launch {
            try {

                val newTask = Task(
                    id = ((state.value.tasks.maxOfOrNull { it.id.toIntOrNull() ?: 0 }
                        ?: (0 + 1))).toString(),
                    title = title,
                    status = 0,
                    createdAt = MyDateTimeUtil.currentDate()
                )

                val postedTask = repository.postTask(newTask)

                _state.value = _state.value.copy(
                    tasks = _state.value.tasks + postedTask
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message ?: "Failed to post task")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun addGroupedTask(title: String) {
        viewModelScope.launch {
            try {

                val newTask = Task(
                    id = ((state.value.tasks.maxOfOrNull { it.id.toIntOrNull() ?: 0 }
                        ?: (0 + 1))).toString(),
                    title = title,
                    status = 0,
                    createdAt = MyDateTimeUtil.currentDate()
                )

                val postedTask = repository.postGroupedTask(newTask)

                _state.value = _state.value.copy(
                    groupedTasks = _state.value.groupedTasks + postedTask
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message ?: "Failed to post task")
            }
        }
    }

    fun editTask(updatedTask: Task) {
        viewModelScope.launch {
            try {
                val response = repository.updateTask(updatedTask.id, updatedTask)
                _state.value = _state.value.copy(
                    tasks = _state.value.tasks.map {
                        if (it.id == updatedTask.id) response else it
                    }
                )
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(errorMessage = e.message ?: "Failed to update task")
            }
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            val success = repository.deleteTask(task.id)
            if (success) {
                _state.value = _state.value.copy(
                    tasks = _state.value.tasks.filterNot { it.id == task.id },
                    taskToDelete = null
                )
            } else {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to delete task with id ${task.id}"
                )
            }
        }
    }

    fun getAITasks(userPrompt: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val result = repository.suggestTasks3(userPrompt)
                _state.value = _state.value.copy(tasks = result!!, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun getAIGroupedTasks(userPrompt: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val result = repository.suggestTasks(userPrompt)
                _state.value = _state.value.copy(groupedTasks = result!!,
                    isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun getAiSuggestionText(userPrompt: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val result = repository.suggestedTasks(userPrompt)
                _state.value = _state.value.copy(aiSuggestionTasks = result, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun confirmDelete(task: Task) {
        _state.value = _state.value.copy(taskToDelete = task)
    }

    fun dismissDialog() {
        _state.value = _state.value.copy(taskToDelete = null)
    }

    fun moveTask(fromIndex: Int, toIndex: Int) {
        val updatedList = _state.value.tasks.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }

        _state.value = state.value.copy(tasks = updatedList)
    }


    fun moveGroupedTask(task: Task, newGroupLabel: String) {
        val currentGrouped = _state.value.groupedTasks.toMutableMap()

        // Find which group currently contains this task
        val oldGroupKey = currentGrouped.entries.firstOrNull { entry ->
            entry.value.any { it.id == task.id }
        }?.key ?: return

        if (oldGroupKey == newGroupLabel) return // no change

        // Remove the task from its old group
        val oldGroupTasks = currentGrouped[oldGroupKey]?.toMutableList() ?: mutableListOf()
        oldGroupTasks.removeAll { it.id == task.id }
        currentGrouped[oldGroupKey] = oldGroupTasks

        // Add the task to the new group
        val newGroupTasks = currentGrouped[newGroupLabel]?.toMutableList() ?: mutableListOf()
        newGroupTasks.add(task)
        currentGrouped[newGroupLabel] = newGroupTasks

        _state.value = _state.value.copy(groupedTasks = currentGrouped)
    }

    fun reorderWithinGroup(groupLabel: String, newList: List<Task>) {
        val currentGrouped = _state.value.groupedTasks.toMutableMap()
        currentGrouped[groupLabel] = newList
        _state.value = _state.value.copy(groupedTasks = currentGrouped)
    }

}

