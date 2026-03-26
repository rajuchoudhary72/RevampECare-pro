package com.app.ecarepro.core.domain.model

data class TaskOverviewData(
    val myID: Int = 0,
    val canChangeStatus: Int = 0,
    val overdue: List<TaskItem> = emptyList(),
    val todays: List<TaskItem> = emptyList(),
    val upcoming: List<TaskItem> = emptyList(),
    val closed: List<TaskItem> = emptyList(),
)

data class TaskItem(
    val id: String = "",
    val tlId: Int = 0,
    val tskID: Int = 0,
    val taskList: String = "",
    val taskTitle: String = "",
    val description: String = "",
    val assignTo: List<TaskAssignee> = emptyList(),
    val watchers: List<TaskAssignee> = emptyList(),
    val assignBy: String = "",
    val priority: Int = 0,
    val startDate: String = "",
    val dueDate: String = "",
    val attachment: String = "",
    val imOwner: Boolean = false,
    val imWatcher: Boolean = false,
    val canChangeStatus: Boolean = false,
    val status: Int = 0,
    val overallStatus: Int = 0,
)

data class TaskAssignee(
    val id: String = "",
    val userID: Int = 0,
    val title: String = "",
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
    val status: Int = 0,
)
