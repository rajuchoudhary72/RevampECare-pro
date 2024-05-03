package com.app.ecarepro.data.network.model

data class NetworkClassAssignments(
    val errorCode: Int,
    val message: String,
    val status: String,
    val subjectAssignments: List<SubjectAssignment>
)