package com.app.ecarepro.model

import com.app.ecarepro.data.network.model.Recipient
import com.app.ecarepro.model.SenderDTL

data class Conversation(
    val abbreviation: String,
    val isReplyMsg: Int,
    val msgID: String,
    val msgType: Int,
    val readCount: Int,
    val recipients: List<Recipient>,
    val senderDTL: SenderDTL,
    val sentOn: String,
    val subject: String,
    val id: String
)