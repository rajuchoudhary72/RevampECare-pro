package com.app.ecarepro.core.domain.model

data class Subject(
    val classID: Int?,
    val periodSubject: String?,
    val shortName: String?,
    val subID: Int?,
    val subjectName: String?,
) {
    companion object {
         val SUBJECT_ALL = Subject(null, null, null, null, "All Subjects")
    }
}