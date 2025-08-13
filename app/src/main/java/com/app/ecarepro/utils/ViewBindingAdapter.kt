package com.app.ecarepro.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.decode.SvgDecoder
import coil.load
import com.airbnb.epoxy.EpoxyRecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.TransactionDetail
import com.app.ecarepro.databinding.ItemCollectionBinding
import com.app.ecarepro.databinding.ItemCollectionCollectFooterBinding
import com.app.ecarepro.messageFilePreview
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri


@BindingAdapter("isVisible")
fun View.showOrGone(visible: Boolean) {
    isVisible = visible
}

@BindingAdapter("isInvisible")
fun View.showOrHide(invisible: Boolean) {
    isInvisible = invisible
}
@BindingAdapter("imageUrl", "placeholder", requireAll = false)
fun ImageView.imageUrl(url: String?, placeholder: Drawable? = null) {
    Log.d("ImageDebug1", "Loading image: $url")
    load(url) {
        if (url?.contains("svg") == true)
            decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
        crossfade(true)
        if (placeholder != null) {
            placeholder(placeholder)
            error(placeholder)
        } else {
           /* placeholder(getImagePlaceholder(url))
            error(getImagePlaceholder(url))*/
            placeholder(getResourcePlaceholder(context, url))
            error(getResourcePlaceholder(context, url))
        }
    }
}
const val MIME_TYPE_PDF = "application/pdf"
const val MIME_TYPE_MSWORD = "application/msword"
const val MIME_TYPE_XLS = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
fun getResourcePlaceholder(context: Context, url: String?): Int {
    if (url == null) {
        return R.drawable.img_placeholder
    }

    val mimeType = if (isValidUrl(url)) {
        when (url.substringAfterLast(".", "").lowercase()) {
            "pdf" -> MIME_TYPE_PDF
            "doc", "docx" -> MIME_TYPE_MSWORD
            "xlsx" -> MIME_TYPE_XLS
            "mp3", "wav", "ogg", "flac", "aac", "m4a", "m4b", "m4p", "alac", "aiff",
            "ape", "wv", "mpc", "tak", "opus", "spx", "gsm", "dts", "wma", "au", "aiff", "audio" -> "audio/*"

            else -> null
        }
    } else {
        url.toUri().let { getFileType(context, it) }
    }

    return when (mimeType) {
        MIME_TYPE_PDF -> R.drawable.baseline_file_present_24
        MIME_TYPE_MSWORD -> R.drawable.doc
        MIME_TYPE_XLS -> R.drawable.excelicon
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> R.drawable.doc
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> R.drawable.excelicon
        "image/*" -> R.drawable.img_placeholder
        "audio/*" -> R.drawable.baseline_audio_file_24
        else -> R.drawable.img_placeholder // Placeholder for unknown types
    }
}

fun isValidUrl(url: String): Boolean {
    return url.startsWith("http://") || url.startsWith("https://")
}

fun getFileType(context: Context, fileUri: Uri): String? {
    return context.contentResolver.getType(fileUri)
}



/*fun getImagePlaceholder(url: String?): Int {
    return if (url?.contains("pdf") == true) {
        R.drawable.baseline_file_present_24
    } else if (isAudioUrl(url)) {
        R.drawable.baseline_audio_file_24
    } else if (url?.contains("doc") == true || url?.contains("docx") == true) {
        R.drawable.doc
    } else {
        R.drawable.img_placeholder
    }
}*/


fun isAudioUrl(url: String?): Boolean {
    val audioExtensions = setOf(
        "mp3", "wav", "ogg", "flac", "aac", "m4a", "m4b", "m4p", "alac", "aiff",
        "ape", "wv", "mpc", "tak", "opus", "spx", "gsm", "dts", "wma", "au", "aiff","audio"
    )
    val extension = url?.substringAfterLast(".", "")?.lowercase()
    return audioExtensions.contains(extension)
}


/*@BindingAdapter("imageUrl", "placeholder", requireAll = false)
fun ImageView.imageUrlChat(url: String?, placeholder: Drawable? = null) {
    load(url) {
        if (url?.contains("svg") == true)
            decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
        crossfade(true)
        if (placeholder != null) {
            placeholder(placeholder)
            error(placeholder)
        } else {
            placeholder(R.drawable.img_placeholder)
            error(R.drawable.img_placeholder)
        }
    }
}*/

@BindingAdapter("imageRes")
fun ImageView.imageRes(res: Int?) {
    res?.let {
        setImageResource(res)
    }
}

@BindingAdapter("animateBetweenColorsOnExpand", "colorFrom", "colorTo", requireAll = true)
fun CardView.animateBetweenColorsOnExpand(
    isExpanded: Boolean,
    from: Int,
    to: Int,
) {
    val startColor = if (isExpanded) from else to
    val endColor = if (isExpanded) to else from
    val anim = ValueAnimator()
    anim.setIntValues(startColor, endColor)
    anim.setEvaluator(ArgbEvaluator())
    anim.addUpdateListener { valueAnimator ->
        setCardBackgroundColor(valueAnimator.animatedValue as Int)
    }
    anim.setDuration(1000)
    anim.start()
}

@BindingAdapter("collectionItems")
fun LinearLayout.addCollectionItems(collections: List<TransactionDetail>?) {
    collections ?: return
    removeAllViews()
    collections.forEachIndexed { index, transactionDetail ->
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(context), null, false)
        binding.showDivider = collections.lastIndex != index
        binding.transactionDetail = transactionDetail
        addView(binding.root)

    }
    val footer =
        ItemCollectionCollectFooterBinding.inflate(LayoutInflater.from(context), null, false)
    footer.amount.rupeeText(collections.sumOf { it.amount ?: 0.0 })
    addView(footer.root)
}

@BindingAdapter("files", "clickListener", requireAll = false)
fun EpoxyRecyclerView.buildFilesModel(files: List<String>, clickListener: FileClickListener) {

    withModels {
        files.forEach {
            messageFilePreview {
                id(it)
                image(it)
                onClickPhoto { _ ->
                    clickListener.onClick(it)
                }
            }
        }
    }

}

@BindingAdapter("autoLinkText")
fun TextView.autoLink(textValue: String) {
    text = textValue
    if (textValue.all { it.isDigit() } && textValue.length == 10) {
        Linkify.addLinks(this, Linkify.PHONE_NUMBERS)
    } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(textValue).matches()) {
        Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES)
    }
}

@BindingAdapter("rupeeText")
fun TextView.rupeeText(rupee: Double?) {
    if (rupee==null){
        text = "₹$0.0"
    }else{
        text = "₹$rupee"
    }

}
@BindingAdapter("rupeeText")
fun TextView.rupeeText(rupee: String?) {
    text = "₹$rupee"
}
interface FileClickListener {
    fun onClick(file: String)
}
interface MessageClickListener {
    fun onClick(file: String)
}


@BindingAdapter("htmlText")
fun TextView.htmlText(html: String?) {
    text = Html.fromHtml(html)
}
@BindingAdapter("isBold")
fun TextView.isBold(isBold:Boolean) {
    typeface = ResourcesCompat.getFont(context, if(isBold) R.font.calibri_regular else R.font.calibri_bold)
}
@BindingAdapter("styledText")
fun TextView.setStyledText(text: String?) {
    if (text != "") {
        val ssb = SpannableStringBuilder(text)


        try {
            var cs: CharacterStyle


            val sentence: String? = text

            val boldStartIndexes: List<Int>? = sentence?.let { Constant.boldFindStartIndexes(it) }
            val boldEndIndexes: List<Int>? = sentence?.let { Constant.boldFindEndStarIndexes(it) }


            Log.v("Okkkkk", "Word Start Indexes BOLD : $boldStartIndexes")
            Log.v("Okkkkk", "Word End Indexes BOLD : $boldEndIndexes")


            var boldstart = 0
            var boldend = 0
            var deleteIndesx = 0
            if (boldStartIndexes?.size!! >= 1 && boldEndIndexes?.size !!>= 1) {
                for (i in boldStartIndexes.indices) {
                    boldstart = boldStartIndexes[i]
                    for (j in i until boldEndIndexes.size) {
                        boldend = boldEndIndexes[j]
                        cs = StyleSpan(Typeface.BOLD)
                        ssb.setSpan(cs, boldstart, boldend, 1)
                        break
                    }
                }
            }

            this.text = ssb

            val ssbbb = SpannableStringBuilder(text)

            var dboldstart = 0
            var dboldend = 0
            if (boldStartIndexes.size >= 1 && boldEndIndexes?.size !!>= 1) {
                for (i in boldStartIndexes.indices) {
                    dboldstart = boldStartIndexes[i]
                    for (j in i until boldEndIndexes.size) {
                        dboldend = boldEndIndexes[j]
                        ssbbb.delete(dboldstart - deleteIndesx, dboldstart - deleteIndesx + 1)
                        ssbbb.delete(dboldend - deleteIndesx - 1, dboldend - deleteIndesx)
                        deleteIndesx = deleteIndesx + 2
                        break
                    }
                }
            }

            this.text = ssbbb

            val ssbbbitalic = SpannableStringBuilder(text)

            val sentenceit: String = text

            val italicStartIndexes: List<Int> = Constant.italicFindStartIndexes(sentenceit)
            val italicEndIndexes: List<Int> = Constant.italicFindEndStarIndexes(sentenceit)

            Log.v("Okkkkk", "Word Start Indexes Italic : $italicStartIndexes")
            Log.v("Okkkkk", "Word End Indexes Italic : $italicEndIndexes")


            var italicstart = 0
            var italicdend = 0
            if (italicStartIndexes.size >= 1 && italicEndIndexes.size >= 1) {
                for (i in italicStartIndexes.indices) {
                    italicstart = italicStartIndexes[i]
                    for (j in i until italicEndIndexes.size) {
                        italicdend = italicEndIndexes[j]
                        cs = StyleSpan(Typeface.ITALIC)
                        ssbbbitalic.setSpan(cs, italicstart, italicdend, 1)
                        break
                    }
                }
            }
            this.text = ssbbbitalic


            val ssbbbitalicDelte = SpannableStringBuilder(text)


            var itlicDeleteIndesx = 0
            var ditalicstart = 0
            var ditalicdend = 0
            if (italicStartIndexes.size >= 1 && italicEndIndexes.size >= 1) {
                for (i in italicStartIndexes.indices) {
                    ditalicstart = italicStartIndexes[i]
                    for (j in i until italicEndIndexes.size) {
                        ditalicdend = italicEndIndexes[j]
                        ssbbbitalicDelte.delete(
                            ditalicstart - itlicDeleteIndesx,
                            ditalicstart - itlicDeleteIndesx + 1
                        )
                        ssbbbitalicDelte.delete(
                            ditalicdend - itlicDeleteIndesx - 1,
                            ditalicdend - itlicDeleteIndesx
                        )
                        itlicDeleteIndesx = itlicDeleteIndesx + 2
                        break
                    }
                }
            }
            this.text = ssbbbitalicDelte

            val ssbbbitalicstrikethrough = SpannableStringBuilder(text)

            val sentenceStric: String = text

            val strikethroughStartIndexes: List<Int> =
                Constant.strikethroughFindStartIndexes(sentenceStric)
            val strikethroughEndIndexes: List<Int> =
                Constant.strikethroughFindEndStarIndexes(sentenceStric)

            Log.v(
                "Okkkkk",
                "Word Start Indexes strikethrough : $strikethroughStartIndexes"
            )
            Log.v("Okkkkk", "Word End Indexes strikethrough : $strikethroughEndIndexes")

            var strikethroughstart = 0
            var strikethroughend = 0
            if (strikethroughStartIndexes.size >= 1 && strikethroughEndIndexes.size >= 1) {
                for (i in strikethroughStartIndexes.indices) {
                    strikethroughstart = strikethroughStartIndexes[i]
                    for (j in i until strikethroughEndIndexes.size) {
                        strikethroughend = strikethroughEndIndexes[j]
                        cs = UnderlineSpan()
                        ssbbbitalicstrikethrough.setSpan(
                            cs,
                            strikethroughstart,
                            strikethroughend,
                            1
                        )
                        break
                    }
                }
            }

         //   binding.tvSubject.setText(ssbbbitalicstrikethrough)
            this.text = ssbbbitalicstrikethrough
            val ssbbbitalicstrikethroughDelete =
                SpannableStringBuilder(text)


            var strikethroughstartDeleteIndex = 0
            var strikethroughstartDelete = 0
            var strikethroughendDelete = 0
            if (strikethroughStartIndexes.size >= 1 && strikethroughEndIndexes.size >= 1) {
                for (i in strikethroughStartIndexes.indices) {
                    strikethroughstartDelete = strikethroughStartIndexes[i]
                    for (j in i until strikethroughEndIndexes.size) {
                        strikethroughendDelete = strikethroughEndIndexes[j]
                        ssbbbitalicstrikethroughDelete.delete(
                            strikethroughstartDelete - strikethroughstartDeleteIndex,
                            strikethroughstartDelete - strikethroughstartDeleteIndex + 1
                        )
                        ssbbbitalicstrikethroughDelete.delete(
                            strikethroughendDelete - strikethroughstartDeleteIndex - 1,
                            strikethroughendDelete - strikethroughstartDeleteIndex
                        )
                        strikethroughstartDeleteIndex = strikethroughstartDeleteIndex + 2
                        break
                    }
                }
            }
            this.text = ssbbbitalicstrikethroughDelete
         //   binding.tvSubject.setText(ssbbbitalicstrikethroughDelete)
        } catch (ignored: Exception) {
        }
    }

}