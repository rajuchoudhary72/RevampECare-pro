package com.app.ecarepro.data.network.model

data class FeeBookDownloadRequestModel(
    private val schoolcode: String,
    private val ParentName: String,
    private val installid: Int,
    private val stid: Int,
    private val yrid: Int,
    )
