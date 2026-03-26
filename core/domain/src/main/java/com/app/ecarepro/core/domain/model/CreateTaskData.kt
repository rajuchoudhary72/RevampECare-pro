package com.app.ecarepro.core.domain.model

data class CreateTaskData(
    val canCreateTaskList: Boolean = false,
    val taskLists: List<TaskListItem> = emptyList(),
    val watchers: List<TaskWatcherDomain> = emptyList(),
)

data class TaskListItem(
    val id: Int = 0,
    val title: String = "",
)
