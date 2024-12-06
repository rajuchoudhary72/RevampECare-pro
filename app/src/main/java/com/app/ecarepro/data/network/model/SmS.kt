package com.app.ecarepro.data.network.model

data class SmS(
    val designation: String,
    val mobile: String,
    val photo: String,
    val `receiver`: Recipient,
    val senderName: String,
    val sentOn: String,
    val statusOn: String,
    val smsType: String,
    val status: String,
    val text: String
)