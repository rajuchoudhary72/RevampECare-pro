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
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
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

fun loadSvgFromApi(svgData: String, imageView: ImageView) {
    try {
        // Parse the SVG data string
        val svg = SVG.getFromString(svgData)

        // Set the dimensions of the SVG image
        svg.setDocumentWidth("100%")
        svg.setDocumentHeight("100%")

        // Create a new PictureDrawable from the SVG
        val pictureDrawable = svg.renderToPicture().let { PictureDrawable(it) }

        // Set the PictureDrawable to the ImageView
        imageView.setImageDrawable(pictureDrawable)
    } catch (e: SVGParseException) {
        e.printStackTrace()
        // Handle SVG parsing error
    }
}