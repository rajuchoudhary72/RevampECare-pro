package com.app.ecarepro.feature.taskmanger

import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskItem
import com.app.ecarepro.core.domain.model.TaskOverviewData

fun TaskOverviewData.toPresentation(): TaskManagementData {
    return TaskManagementData(
        overdue = overdue.map { it.toPresentation() },
        todays = todays.map { it.toPresentation() },
        upcoming = upcoming.map { it.toPresentation() },
        closed = closed.map { it.toPresentation() },
    )
}

fun TaskItem.toPresentation(): TaskPresentation {
    return TaskPresentation(
        id = id,
        taskTitle = taskTitle,
        description = description,
        taskList = taskList,
        priority = TaskPriority.fromValue(priority),
        status = TaskStatus.fromValue(status),
        startDate = startDate,
        dueDate = dueDate,
        assignees = assignTo.map { it.toPresentation() },
        attachment = attachment,
    )
}

fun TaskAssignee.toPresentation(): TaskAssigneePresentation {
    return TaskAssigneePresentation(
        id = userID,
        name = name,
        designation = designation,
        photo = photo,
        status = status,
    )
}
