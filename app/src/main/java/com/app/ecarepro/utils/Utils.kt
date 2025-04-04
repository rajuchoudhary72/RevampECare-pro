package com.app.ecarepro.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.app.ecarepro.R
import com.app.ecarepro.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.String.format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.app.ecarepro.ui.CalenderInstance
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream

fun Int.toPx(context: Context) =
    (this * context.resources.displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT

fun Context.getColorRes(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun stringFormat2String(stringId1: MainActivity, stringId: Int, value1: String?, value2: String?): String? {
    return format(
        stringId1.getResources().getString(stringId),
        value1,
        value2
    )
}
fun ensureHttps(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        "https://$url"
    }
}
fun currentDate(): String {
    val c = Calendar.getInstance().time
    println("Current time => $c")
    val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    return df.format(c)
}
fun calenderInstance(){
    val calendar = Calendar.getInstance()
    CalenderInstance.currentYear = calendar[Calendar.YEAR]
    CalenderInstance.currentMonth = calendar[Calendar.MONTH]
    CalenderInstance.currentDateDD = calendar[Calendar.DATE]
}
fun getDayNumberSuffix(day: Int): String {
    if (day >= 11 && day <= 13) {
        return "th"
    }
    return when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}
fun date_converterDay(s: String?): String {
    if (s==null){
        return 0.toString()
    }
    val isoFormat =
        SimpleDateFormat("dd-MMM-yyyy")
    isoFormat.timeZone = TimeZone.getDefault()
    val isoFormatDay =
        SimpleDateFormat("dd")
    var date: Date? = null
    try {

        date = isoFormat.parse(s)
    } catch (e: ParseException) {
        e.printStackTrace()
        return s.split("-")[0]
    }
    return isoFormatDay.format(date)
}

fun dateToMonth(s: String?): String {
    val isoFormat =
        SimpleDateFormat("yyyy-MM-dd")
    isoFormat.timeZone = TimeZone.getDefault()
    val isoFormatDay =
        SimpleDateFormat("MM")
    var date: Date? = null
    try {
        date = isoFormat.parse(s)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return isoFormatDay.format(date)
}

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
    try {
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
    }catch (e:Exception){
        return ""
    }

}


 fun shareImageFromUrl(context: Context, imageUrl: String) {
    Glide.with(context)
        .asBitmap()
        .load(imageUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs() // don't forget to make the directory
                val stream = FileOutputStream("$cachePath/image.png") // overwrites this image every time
                resource.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val imagePath = File(context.cacheDir, "images")
                val newFile = File(imagePath, "image.png")
                val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.myFileProvider", newFile)

                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    context.startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Handle case when the image load is cleared
            }
        })
}

 fun shareUrl(context: Context, url: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}