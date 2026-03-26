package com.app.ecarepro.core.network.model.library

import com.app.ecarepro.core.domain.model.library.LibraryBook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLibraryBookDetail(
    @SerialName("bookID") val bookID: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("storageHint") val storageHint: String? = null,
    @SerialName("publicationYear") val publicationYear: String? = null,
    @SerialName("bookNo") val bookNo: String? = null,
    @SerialName("classNo") val classNo: String? = null,
    @SerialName("coverImg") val coverImg: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("publication") val publication: String? = null,
    @SerialName("cost") val cost: String? = null,
    @SerialName("fineAmount") val fineAmount: Double? = null,
    @SerialName("isDelayed") val isDelayed: Boolean? = null,
    @SerialName("eBookLink") val eBookLink: String? = null,
    @SerialName("issuable") val issuable: String? = null,
    @SerialName("accessionNumber") val accessionNumber: String? = null,
    @SerialName("accessionNo") val accessionNo: String? = null,
    @SerialName("issueDate") val issueDate: String? = null,
    @SerialName("expReturnDate") val expReturnDate: String? = null,
    @SerialName("returnDate") val returnDate: String? = null,
)

fun NetworkLibraryBookDetail.toDomainModel() = LibraryBook(
    bookID = bookID ?: 0,
    title = title.orEmpty(),
    author = author.orEmpty(),
    subject = subject.orEmpty(),
    storageHint = storageHint.orEmpty(),
    publicationYear = publicationYear.orEmpty(),
    bookNo = bookNo.orEmpty(),
    classNo = classNo.orEmpty(),
    coverImg = coverImg,
    status = status.orEmpty(),
    publication = publication,
    cost = cost,
    fineAmount = fineAmount,
    isDelayed = isDelayed ?: false,
    issuable = issuable,
    accessionNumber = accessionNumber ?: accessionNo.orEmpty(),
    issuedOn = issueDate,
    expectedReturnDate = expReturnDate,
    returnOn = returnDate,
)
