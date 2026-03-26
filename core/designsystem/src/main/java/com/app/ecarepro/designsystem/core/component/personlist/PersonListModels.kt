package com.app.ecarepro.designsystem.core.component.personlist

import androidx.compose.runtime.Immutable

// Gender Enum
enum class Gender {
    MALE, FEMALE, OTHER;

    companion object {
        fun fromString(value: String): Gender {
            return when (value.lowercase()) {
                "male", "m" -> MALE
                "female", "f" -> FEMALE
                else -> OTHER
            }
        }
    }
}

// View Mode Enum
enum class ListViewMode {
    GRID, LIST
}

// Person Type Enum (for navigation)
enum class PersonType {
    STAFF,
    STUDENT,
    PARENT
}

// Person Presentation (View-ready model)
@Immutable
data class PersonPresentation(
    val id: String,
    val name: String,
    val subtitle: String,      // e.g., "Designation: TGT" or "Class: 10th-A | Roll: 15"
    val detail: String,        // e.g., "DOJ: 01 July 2012" or "Adm No: 2024001"
    val profileImageURL: String?,
    val gender: Gender,
    val personType: PersonType  // Used for navigation to appropriate profile screen
)

// Stats Presentation
@Immutable
data class ListStatsPresentation(
    val total: Int,
    val maleCount: Int,
    val femaleCount: Int,
    val maleLabel: String,     // "Male" for staff, "Boys" for students
    val femaleLabel: String    // "Female" for staff, "Girls" for students
) {
    companion object {
        val EMPTY = ListStatsPresentation(0, 0, 0, "Male", "Female")
    }
}

// Filter Section
@Immutable
data class FilterSection(
    val id: String,
    val title: String,
    val options: List<String>
)

// List Configuration
@Immutable
data class PersonListConfiguration(
    val title: String,
    val searchPlaceholder: String,
    val emptyTitle: String,
    val emptySubtitle: String,
    val statsLabels: Pair<String, String>,  // (maleLabel, femaleLabel)
    val showScholarTypeDropdown: Boolean,
    val showClassTabs: Boolean
) {
    companion object {
        val STAFF = PersonListConfiguration(
            title = "Staff List",
            searchPlaceholder = "Search by name, designation, mobile...",
            emptyTitle = "No Staff Found",
            emptySubtitle = "There are no records available at the moment.",
            statsLabels = Pair("Male", "Female"),
            showScholarTypeDropdown = false,
            showClassTabs = false
        )

        val STUDENT = PersonListConfiguration(
            title = "Students List",
            searchPlaceholder = "Search by name, admission number...",
            emptyTitle = "No Students Found",
            emptySubtitle = "There are no records available for the selected criteria.",
            statsLabels = Pair("Boys", "Girls"),
            showScholarTypeDropdown = true,
            showClassTabs = true
        )
    }
}
