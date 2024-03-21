package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.SubjectAssignment

data class NetworkAssignments(
    val errorCode: Int,
    val message: String,
    val status: String,
    val subjectAssignments: List<SubjectAssignment>
)