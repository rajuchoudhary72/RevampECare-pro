package com.app.ecarepro.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.app.ecarepro.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.ImageView;
import java.io.StringReader;
fun Int.toPx(context: Context) =
    (this * context.resources.displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT

fun Context.getColorRes(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)


fun Context.progressDialog(): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setView(R.layout.loading_bar)
        .setBackground(ColorDrawable(Color.TRANSPARENT))
        .show()
}

