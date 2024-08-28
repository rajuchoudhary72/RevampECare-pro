package com.app.ecarepro.ui.fom_guard.model.verify_code

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val appdetails: Appdetails,
    val message: String,
    val status: Boolean
) : Parcelable