package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Assignment

data class SubjectAssignment(
    val assignments: List<Assignment>,
    val showAlert: Boolean,
    val subID: Int,
    val subject: String
)