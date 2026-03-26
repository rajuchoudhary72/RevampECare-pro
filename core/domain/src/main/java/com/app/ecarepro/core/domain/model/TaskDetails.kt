package com.app.ecarepro.core.domain.model

data class TaskDetailsDomain(
    val task: TaskInfoDomain? = null,
    val activities: List<TaskActivityDomain> = emptyList(),
    val comments: List<TaskCommentDomain> = emptyList(),
    val myID: Int = 0,
)

data class TaskInfoDomain(
    val id: String = "",
    val taskTitle: String = "",
    val description: String = "",
    val assignBy: String = "",
    val assignTo: List<TaskAssignee> = emptyList(),
    val startDate: String = "",
    val dueDate: String = "",
    val priority: Int = 0,
    val status: Int = 0,
    val attachment: String = "",
    val imOwner: Boolean = false,
    val imWatcher: Boolean = false,
    val watchers: List<TaskWatcherDomain> = emptyList(),
    val tlId: Int = 0,
    val taskList: String = "",
    val tskID: Int = 0,
    val canChangeStatus: Boolean = false,
)

data class TaskCommentDomain(
    val comment: String = "",
    val commentBy: Int = 0,
    val commentOn: String = "",
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
)

data class TaskActivityDomain(
    val activity: String = "",
    val actionOn: String = "",
    val actorID: Int = 0,
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
)

data class TaskWatcherDomain(
    val userID: Int = 0,
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
    val status: Int = 0,
    val title: String = "",
    val id: String = "",
)
