package com.app.ecarepro.data.network.model.submit_assignment

data class PostSubmitAssignment(
    val asgID: Int,
    val attachment: Attachment,
    val `data`: String,
    val fileName: String,
    val id: String
)