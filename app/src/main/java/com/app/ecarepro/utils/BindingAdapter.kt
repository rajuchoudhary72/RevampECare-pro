package com.app.ecarepro.utils

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.app.ecarepro.R
import com.app.ecarepro.utils.Constant.Companion.boldFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.boldFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindStartIndexes
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
    if (show) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_edit)
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    } else {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }
}


@BindingAdapter("formattedText")
fun setFormattedText(textView: TextView, text: String?) {
    if (text != null) {
        if (text.contains("~*") || text.contains("*~")) {
            //bold with  underline
            val formattedText = formatBoldUnderlineText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else if (text.contains("_*") || text.contains("*_")) {
            //bold with  Italic
            val formattedText = formatBoldItalicText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else if (text.contains("~_") || text.contains("_~")) {
            //underline  with  Italic
            val formattedText = formatItalicUnderlineText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else if (text.contains("_~") || text.contains("~_")) {
            //underline  with  Italic
            val formattedText = formatItalicUnderlineText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else if (text.contains("_~*") || text.contains("*~_")) {
            //underline  with  Italic with  bold
            val formattedText = formatBoldItalicUnderlineText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else if (text.contains("~_*") || text.contains("*_~")) {
            //underline  with  Italic with  bold
            val formattedText = formatBoldItalicUnderlineText(text)
            // Set the formatted text to a TextView
            textView.text = formattedText
        } else {
            // only  single property  like only bold  ,  underline  , Italic
            if (text != null) {
                textView.text = formatAnyOneBoldItalicUnderlineText(text)
            } else {
                textView.text = ""
            }
        }

    }
}

data class SpanInfo(val start: Int, val end: Int, val style: Any)

fun formatAnyOneBoldItalicUnderlineText(input: String): SpannableString {
    val cleanedText = StringBuilder()
    val spans = mutableListOf<SpanInfo>()

    // Regex pattern to capture all formatting markers and nested content
    val regex =
        "(\\*[^*_~]+\\*|_[^*_~]+_|~[^*_~]+~|\\*[^*_~]+_[^*_~]+_\\*|\\*[^*_~]+~[^*_~]+~\\*|_[^*_~]+~[^*_~]+~_\\*|\\*[^*_~]+_[^*_~]+~[^*_~]+~_\\*)".toRegex()

    var currentIndex = 0

    while (currentIndex < input.length) {
        val match = regex.find(input, currentIndex)

        if (match != null && match.range.first == currentIndex) {
            val matchedText = match.value
            val rawText = matchedText.replace("[*_~]".toRegex(), "") // Remove markers

            val start = cleanedText.length
            cleanedText.append(rawText)
            val end = cleanedText.length

            // Apply spans based on markers
            if (matchedText.startsWith("*") && matchedText.endsWith("*")) {
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.BOLD)))
            }
            if (matchedText.startsWith("_") && matchedText.endsWith("_")) {
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.ITALIC)))
            }
            if (matchedText.startsWith("~") && matchedText.endsWith("~")) {
                spans.add(SpanInfo(start, end, UnderlineSpan()))
            }
            // Handle combinations
            if (matchedText.contains("*") && matchedText.contains("_")) {
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.BOLD)))
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.ITALIC)))
            }
            if (matchedText.contains("*") && matchedText.contains("~")) {
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.BOLD)))
                spans.add(SpanInfo(start, end, UnderlineSpan()))
            }
            if (matchedText.contains("_") && matchedText.contains("~")) {
                spans.add(SpanInfo(start, end, UnderlineSpan()))
                spans.add(SpanInfo(start, end, StyleSpan(Typeface.ITALIC)))
            }

            currentIndex = match.range.last + 1
        } else {
            cleanedText.append(input[currentIndex])
            currentIndex++
        }
    }

    val spannable = SpannableString(cleanedText.toString())
    spans.forEach { span ->
        spannable.setSpan(span.style, span.start, span.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return spannable
}

fun formatBoldUnderlineText(input: String): SpannableStringBuilder {
    val spannableBuilder = SpannableStringBuilder()
    val regex = "~\\*(.*?)\\*~".toRegex() // Matches ~*text*~

    var lastIndex = 0
    regex.findAll(input).forEach { matchResult ->
        // Append text before the matched part
        spannableBuilder.append(input.substring(lastIndex, matchResult.range.first))

        // Extract the matched text
        val matchedText = matchResult.groups[1]?.value ?: ""

        // Apply formatting (bold + underline)
        val spannable = SpannableString(matchedText)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Add the formatted text to the builder
        spannableBuilder.append(spannable)

        lastIndex = matchResult.range.last + 1
    }

    // Append remaining text after the last match
    spannableBuilder.append(input.substring(lastIndex))
    return spannableBuilder
}

fun formatBoldItalicText(input: String): SpannableStringBuilder {
    val spannableBuilder = SpannableStringBuilder()
    val regex = "_\\*(.*?)\\*_".toRegex() // Matches _*text*_

    var lastIndex = 0
    regex.findAll(input).forEach { matchResult ->
        // Append unformatted text before the match
        spannableBuilder.append(input.substring(lastIndex, matchResult.range.first))

        // Extract the matched text
        val matchedText = matchResult.groups[1]?.value ?: ""

        // Apply bold and italic formatting
        val spannable = SpannableString(matchedText)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD_ITALIC),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Add the formatted text to the builder
        spannableBuilder.append(spannable)

        lastIndex = matchResult.range.last + 1
    }

    // Append remaining text after the last match
    spannableBuilder.append(input.substring(lastIndex))
    return spannableBuilder
}

fun formatItalicUnderlineText(input: String): SpannableStringBuilder {
    val spannableBuilder = SpannableStringBuilder()
    val regex = "~_(.*?)_~".toRegex() // Matches ~_text_~

    var lastIndex = 0
    regex.findAll(input).forEach { matchResult ->
        // Append plain text before the match
        spannableBuilder.append(input.substring(lastIndex, matchResult.range.first))

        // Extract matched text
        val matchedText = matchResult.groups[1]?.value ?: ""

        // Create Spannable for italic + underline
        val spannable = SpannableString(matchedText)
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Append formatted text
        spannableBuilder.append(spannable)

        lastIndex = matchResult.range.last + 1
    }

    // Append remaining text after the last match
    spannableBuilder.append(input.substring(lastIndex))
    return spannableBuilder
}

fun formatBoldItalicUnderlineText(input: String): SpannableStringBuilder {
    val spannableBuilder = SpannableStringBuilder()
    val regex = "~_\\*(.*?)\\*_~".toRegex() // Matches ~_*text*_~

    var lastIndex = 0
    regex.findAll(input).forEach { matchResult ->
        // Append plain text before the match
        spannableBuilder.append(input.substring(lastIndex, matchResult.range.first))

        // Extract matched text
        val matchedText = matchResult.groups[1]?.value ?: ""

        // Create Spannable for bold + italic + underline
        val spannable = SpannableString(matchedText)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD_ITALIC),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Append formatted text
        spannableBuilder.append(spannable)

        lastIndex = matchResult.range.last + 1
    }

    // Append remaining text after the last match
    spannableBuilder.append(input.substring(lastIndex))
    return spannableBuilder
}
