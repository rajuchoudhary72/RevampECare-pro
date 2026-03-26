package com.app.ecarepro.core.network.model.ebook

import com.app.ecarepro.core.domain.model.ebook.EBook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkEBookItem(
    @SerialName("bookID") val bookID: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("coverImg") val coverImg: String? = null,
    @SerialName("accessionNo") val accessionNo: String? = null,
    @SerialName("publicationYear") val publicationYear: String? = null,
    @SerialName("bookNo") val bookNo: String? = null,
    @SerialName("classNo") val classNo: String? = null,
    @SerialName("issuable") val issuable: String? = null,
    @SerialName("status") val status: String? = null,
)

fun NetworkEBookItem.toDomainModel() = EBook(
    bookID = bookID ?: 0,
    title = title.orEmpty(),
    author = author.orEmpty(),
    coverImg = coverImg,
    accessionNo = accessionNo.orEmpty(),
)
