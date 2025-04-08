package com.app.ecarepro.model

data class PostComplianceData(
    val uType:Int,
    val browsedFile: BrowsedFile?,
    val compliance: String?,
    val id: String?
)