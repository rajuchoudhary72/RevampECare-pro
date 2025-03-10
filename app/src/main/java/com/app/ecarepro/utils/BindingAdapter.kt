package com.app.ecarepro.utils

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.app.ecarepro.R
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String) {

    Picasso.get().load(url).placeholder(R.drawable.default_profile)
        .into(imageView)
}
@BindingAdapter("cardBgColor")
fun MaterialCardView.cardBackgroundColor(colorCode: String?) {
    setCardBackgroundColor(
        if (colorCode.isNullOrEmpty()) ContextCompat.getColor(
            context,
            R.color.category7
        ) else Color.parseColor(colorCode)
    )
}

@BindingAdapter("taskPriorityColor")
fun ImageView.taskPriorityColor(priority: Int) {
    val colorId = when (priority) {
        0 -> {
            R.color.grey_40
        }

        1 -> {
            com.asynctaskcoffee.audiorecorder.R.color.soft_blue
        }

        2 -> {
            R.color.high_periopty
        }

        else -> {
            com.lassi.R.color.colorAccent

        }
    }
    imageTintList = ContextCompat.getColorStateList(context, colorId)

}
@BindingAdapter("taskPriorityTextColor")
fun TextView.taskPriorityTextColor(priority: Int) {
    val colorId = when (priority) {
        0 -> {
            text = "Low"
            R.color.grey_40
        }
        1 -> {
            text = "Normal"
            com.asynctaskcoffee.audiorecorder.R.color.soft_blue
        }
        2 -> {
            text = "High"
            R.color.red
        }
        else -> {
            text = "Urgent"
            com.lassi.R.color.colorAccent
        }
    }
    setTextColor(ContextCompat.getColor(context, colorId))
}
@BindingAdapter("taskStatusColor")
fun ImageView.taskStatusColor(priority: Int) {
    val colorId = when (priority) {
        -1 -> {
            R.color.grey_80
        }

        0 -> {
            R.color.grey_60
        }

        1 -> {
            R.color.pending_color
        }

        2 -> {
            R.color.red
        }

        else -> {
            R.color.green
        }
    }
    imageTintList = ContextCompat.getColorStateList(context, colorId)
}
@BindingAdapter("taskTextPriorityColor")
fun TextView.taskTextPriorityColor(priority: Int) {
    val colorId = when (priority) {
        0 -> {
            text = "Open"
            R.color.grey_40
        }
        1 -> {
            text = "In Progress"
            com.asynctaskcoffee.audiorecorder.R.color.soft_blue
        }
        2 -> {
            text = "Hold"
            R.color.red
        }
        else -> {
            text = "Closed"
            com.lassi.R.color.colorAccent
        }
    }
    setTextColor(ContextCompat.getColor(context, colorId))
}
@BindingAdapter("taskTextStatusColor")
fun TextView.taskTextStatusColor(priority: Int) {
    val colorId = when (priority) {
        -1 -> {
            R.color.grey_80
        }
        0 -> {
            text = "Open"
            R.color.grey_60
        }
        1 -> {
            text = "In Progress"
            R.color.pending_color
        }
        2 -> {
            text = "Hold"
            R.color.red
        }
        else -> {
            text = "Closed"
            R.color.green
        }
    }
    setTextColor(ContextCompat.getColor(context, colorId))
}

@BindingAdapter("showEditButton")
fun TextView.showEditButton(show: Boolean) {
    if(show){
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_edit)
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }else{
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }
}


@BindingAdapter("formattedText")
fun setFormattedText(textView: TextView, text: String?) {
    if (text != null) {
        // Create a SpannableStringBuilder to build the formatted text
        val spannableString = SpannableStringBuilder()

        // Use regex to find all *...* wrapped text
        val pattern = "\\*(.*?)\\*".toRegex()
        val matches = pattern.findAll(text)

        var lastEnd = 0

        // Loop through each match and apply bold formatting
        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1 // End should include the trailing '*'

            // Append text before the match
            spannableString.append(text.substring(lastEnd, start))

            // Extract the bold text without * characters
            val boldText = match.groups[1]?.value ?: ""
            spannableString.append(boldText)

            // Apply bold style to the extracted text
            val boldStart = spannableString.length - boldText.length
            val boldEnd = spannableString.length

            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                boldStart,
                boldEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Update lastEnd to the end of the current match
            lastEnd = end
        }

        // Append any remaining text after the last match
        if (lastEnd < text.length) {
            spannableString.append(text.substring(lastEnd))
        }

        // Set the formatted text to the TextView
        textView.text = spannableString
    } else {
        textView.text = ""
    }
}