package com.app.ecarepro.model

data class SubjectAssignment(
    val assignments: List<Assignment>,
    val showAlert: Boolean,
    val subID: Int,
    val subject: String
)