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
    if(show){
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_edit)
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }else{
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }
}

fun formatServerText(input: String): SpannableStringBuilder {
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
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Add the formatted text to the builder
        spannableBuilder.append(spannable)

        lastIndex = matchResult.range.last + 1
    }

    // Append remaining text after the last match
    spannableBuilder.append(input.substring(lastIndex))
    return spannableBuilder
}
@BindingAdapter("formattedText")
fun setFormattedText(textView: TextView, text: String?) {
    if (text != null) {
        if (text.contains("~*")||text.contains("*~")){
            val formattedText = formatServerText(text)
// Set the formatted text to a TextView
            textView.text = formattedText
        }else{
            if (text != null) {
                textView.text = formatText(text)
            } else {
                textView.text = ""
            }
        }

    }
}


fun formatText(input: String): SpannableString {
    val cleanedText = StringBuilder()
    val spans = mutableListOf<SpanInfo>()

    // Regex pattern to capture all formatting markers and nested content
    val regex = "(\\*[^*_~]+\\*|_[^*_~]+_|~[^*_~]+~|\\*[^*_~]+_[^*_~]+_\\*|\\*[^*_~]+~[^*_~]+~\\*|_[^*_~]+~[^*_~]+~_\\*|\\*[^*_~]+_[^*_~]+~[^*_~]+~_\\*)".toRegex()

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

data class SpanInfo(val start: Int, val end: Int, val style: Any)
fun formatTextWithUnderline(text: String): SpannableStringBuilder {

    val spannable = SpannableStringBuilder(text)



    // Assuming bold is indicated by `*` characters

    var startIndex = text.indexOf("*")

    var endIndex = 0

    while (startIndex != -1) {

        endIndex = text.indexOf("*", startIndex + 1)

        if (endIndex != -1) {

            val boldText = text.substring(startIndex + 1, endIndex)

            spannable.setSpan(UnderlineSpan(), startIndex + 1, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        }

        startIndex = text.indexOf("*", endIndex)

    }



    return spannable

}

fun getFormatedString(data: String?): SpannableStringBuilder? {
    val ssb = SpannableStringBuilder(data)
    try {
        val boldStartIndexes: List<Int>? = data?.let { boldFindStartIndexes(it) }
        val boldEndIndexes: List<Int>? = data?.let { boldFindEndStarIndexes(it) }
        val italicStartIndexes: List<Int>? = data?.let { italicFindStartIndexes(it) }
        val italicEndIndexes: List<Int>? = data?.let { italicFindEndStarIndexes(it) }
        val strikethroughStartIndexes: List<Int>? = data?.let { strikethroughFindStartIndexes(it) }
        val strikethroughEndIndexes: List<Int>? = data?.let { strikethroughFindEndStarIndexes(it) }
        var cs: CharacterStyle
        var deleteIndesx = 0
        var boldstart = 0
        var boldend = 0
        var len = 0
        if (boldEndIndexes != null) {
            if (boldStartIndexes?.size!! >= 1 && boldEndIndexes.size >= 1) {
                for (i in boldStartIndexes.indices) {
                    boldstart = boldStartIndexes[i]
                    if (boldEndIndexes != null) {
                        for (j in i until boldEndIndexes.size) {
                            boldend = boldEndIndexes[j]
                            cs = StyleSpan(Typeface.BOLD)
                            len = ssb.length
                            if (boldstart == 0) {
                                ssb.setSpan(cs, boldstart, boldend, 1)
                                ssb.delete(boldstart, boldstart + 1)
                                ssb.delete(boldend - 1, boldend)
                            } else {
                                ssb.setSpan(cs, boldstart - deleteIndesx, boldend - deleteIndesx, 1)
                                ssb.delete(boldstart - deleteIndesx, boldstart - deleteIndesx + 1)
                                ssb.delete(boldend - deleteIndesx - 1, boldend - deleteIndesx)
                            }
                            deleteIndesx = deleteIndesx + 2
                            len = 0
                            break
                        }
                    }
                }
            }
        }
        var italicstart = 0
        var italicdend = 0
        if (italicStartIndexes?.size!! >= 1 && italicEndIndexes?.size!! >= 1) {
            for (i in italicStartIndexes.indices) {
                italicstart = italicStartIndexes[i]
                for (j in i until italicEndIndexes?.size!!) {
                    italicdend = italicEndIndexes[j]
                    cs = StyleSpan(Typeface.ITALIC)
                    if (italicstart == 0) {
                        ssb.setSpan(cs, italicstart, italicdend, 1)
                        ssb.delete(italicstart, italicstart + 1)
                        ssb.delete(italicdend - 1, italicdend)
                    } else {
                        ssb.setSpan(
                            cs,
                            italicstart - deleteIndesx,
                            italicdend - deleteIndesx,
                            1
                        )
                        ssb.delete(italicstart - deleteIndesx, italicstart - deleteIndesx + 1)
                        ssb.delete(italicdend - deleteIndesx - 1, italicdend - deleteIndesx)
                    }
                    deleteIndesx = deleteIndesx + 2
                    break
                }
            }
        }
        var strikethroughstart = 0
        var strikethroughend = 0
        if (strikethroughStartIndexes?.size!! >= 1 && strikethroughEndIndexes?.size!! >= 1) {
            for (i in strikethroughStartIndexes.indices) {
                strikethroughstart = strikethroughStartIndexes[i]
                for (j in i until strikethroughEndIndexes.size) {
                    strikethroughend = strikethroughEndIndexes[j]
                    cs = UnderlineSpan()
                    if (strikethroughstart == 0) {
                        ssb.setSpan(cs, strikethroughstart, strikethroughend, 1)
                        ssb.delete(strikethroughstart, strikethroughstart + 1)
                        ssb.delete(strikethroughend - 1, strikethroughend)
                    } else {
                        ssb.setSpan(
                            cs,
                            strikethroughstart - deleteIndesx,
                            strikethroughend - deleteIndesx,
                            1
                        )
                        ssb.delete(
                            strikethroughstart - deleteIndesx,
                            strikethroughstart - deleteIndesx + 1
                        )
                        ssb.delete(
                            strikethroughend - deleteIndesx - 1,
                            strikethroughend - deleteIndesx
                        )
                    }
                    deleteIndesx = deleteIndesx + 2
                    break
                }
            }
        }
    } catch (ignored: Exception) {
    }
    return ssb
}