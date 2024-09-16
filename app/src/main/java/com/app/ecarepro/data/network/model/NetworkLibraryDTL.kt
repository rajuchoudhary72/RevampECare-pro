package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AddBook
import com.app.ecarepro.model.LatestBook

data class NetworkLibraryDTL(
    val addBook: AddBook,
    val errorCode: Int,
    val latestBook: List<LatestBook>?,
    val megaBookLink: String,
    val message: String,
    val myAccount: List<MyAccount>?,
    val status: String
)