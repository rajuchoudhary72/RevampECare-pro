package com.app.ecarepro.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.app.ecarepro.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Int.toPx(context: Context) =
    (this * context.resources.displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT

fun Context.getColorRes(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)


fun Context.progressDialog(): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setView(R.layout.loading_bar)
        .setCancelable(false)
        .setBackground(ColorDrawable(Color.TRANSPARENT))
        .show()
}


const val E_MMM_DD_YYYY_HH_MM_A = "E MMM dd, yyyy hh:mm a"
const val DD_MMM_YYYY = "dd MMM, yyyy"
const val HH_MM_A = "hh:mm a"
fun formatDate(dateString: String, fromDateFormat: String, toDateFormat: String): String {
    try {
        // Parse the input date string into a Date object
        val inputFormat = SimpleDateFormat(fromDateFormat, Locale.getDefault())
        val date = inputFormat.parse(dateString)

        // Format the Date object into the desired output format
        val outputFormat = SimpleDateFormat(toDateFormat, Locale.getDefault())
        return outputFormat.format(date)
    } catch (e: Exception) {
        return ""
    }
}

fun getIcNoProfileBig(context: Context): VectorDrawableCompat? {
    return VectorDrawableCompat.create(context.resources, R.drawable.ic_no_profile_big, null)
}
fun getDateTimeFormatted(DateTime: String): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    simpleDateFormat.timeZone = TimeZone.getDefault()
    var dateTime: Date? = null
    try {
        dateTime = simpleDateFormat.parse(DateTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val finalDate = SimpleDateFormat("dd MMM, yyyy")
    val finalTime = SimpleDateFormat("hh:mm aa")
    val formattedDate = finalDate.format(dateTime).toString()
    val formattedTime = finalTime.format(dateTime).toString().uppercase(Locale.getDefault())
    return "$formattedDate at $formattedTime"
}