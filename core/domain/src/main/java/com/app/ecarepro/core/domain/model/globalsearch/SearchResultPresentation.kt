package com.app.ecarepro.core.domain.model.globalsearch

import com.app.ecarepro.core.domain.model.menu.MenuItem
import javax.annotation.concurrent.Immutable

@Immutable
data class SearchResultPresentation(
    val id: String,
    val type: SearchResultType,
    val name: String,
    val subtitle: String,
    val photoUrl: String,
    val moduleIconUrl: String?,
    val studentId: Int?,
    val staffId: Int?,
    val menuId: Int?,
) {
    companion object {
        fun fromStudent(student: SearchStudent) = SearchResultPresentation(
            id = "student_${student.stID}",
            type = SearchResultType.STUDENT,
            name = student.name,
            subtitle = listOf(student.studentClass, student.contactMob)
                .filter { it.isNotEmpty() }
                .joinToString(" • "),
            photoUrl = student.photo,
            moduleIconUrl = null,
            studentId = student.stID,
            staffId = null,
            menuId = null,
        )

        fun fromStaff(staff: SearchStaff) = SearchResultPresentation(
            id = "staff_${staff.sid}",
            type = SearchResultType.STAFF,
            name = staff.name,
            subtitle = listOf(staff.designation, staff.mobile)
                .filter { it.isNotEmpty() }
                .joinToString(" • "),
            photoUrl = staff.photo,
            moduleIconUrl = null,
            studentId = null,
            staffId = staff.sid,
            menuId = null,
        )

        fun fromMenuItem(menuItem: MenuItem, categoryName: String) = SearchResultPresentation(
            id = "module_${menuItem.id}",
            type = SearchResultType.MODULE,
            name = menuItem.title,
            subtitle = categoryName,
            photoUrl = "",
            moduleIconUrl = menuItem.iconUrl,
            studentId = null,
            staffId = null,
            menuId = menuItem.id,
        )
    }
}

enum class SearchResultType { STUDENT, STAFF, MODULE }

enum class SearchFilterType {
    ALL, MODULES, STUDENTS, STAFF;

    val displayName: String
        get() = when (this) {
            ALL -> "All"
            MODULES -> "Modules"
            STUDENTS -> "Students"
            STAFF -> "Staff"
        }
}
