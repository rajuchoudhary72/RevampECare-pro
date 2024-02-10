package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AddBook
import com.app.ecarepro.model.LatestBook
import javax.annotation.Nullable

data class NetworkLatestBook(
    val addBook: AddBook,
    val errorCode: Int,
    val latestBook: List<LatestBook>,
    val megaBookLink: String,
    val message: String,
    val myAccount: Nullable,
    val status: String
)