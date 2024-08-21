package com.app.ecarepro.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import android.widget.AutoCompleteTextView
import com.app.ecarepro.R

@BindingAdapter("inputText")
fun AutoCompleteTextView.setInputText(text: CharSequence?) {
    if (setText(this, text)) {
        // We already know the text differs, but find the last edit location and use
        // that as the selection.
        setSelection(this.length())
    }
}

private fun setText(textView: AutoCompleteTextView, text: CharSequence?): Boolean {
    if (!isTextDifferent(text, textView.text)) {
        // Previous text is the same. No op
        return false
    }
    textView.setText(text, false)
    return true
}

@BindingAdapter("showDropdownOnClick")
fun AutoCompleteTextView.showDropdownOnClick(show: Boolean?) {

    setOnClickListener {
        if (show == true) {
            showDropDown()
        }
    }
}

@BindingAdapter("inputText")
fun EditText.setInputText(text: CharSequence?) {
    if (setText(this, text)) {
        // We already know the text differs, but find the last edit location and use
        // that as the selection.
        setSelection(this.length())
    }
}

private fun setText(textView: TextView, text: CharSequence?): Boolean {
    if (!isTextDifferent(text, textView.text)) {
        // Previous text is the same. No op
        return false
    }
    textView.text = text
    return true
}

private fun isTextDifferent(str1: CharSequence?, str2: CharSequence?): Boolean = when {
    str1 === str2 -> false
    str1 == null || str2 == null -> true
    str1.length != str2.length -> true
    else -> str1.toString() != str2.toString() // Needed in case either string is a Spannable
}

@BindingAdapter("textWatcher")
fun TextView.setTextWatcher(textWatcher: TextWatcher?) {
    setTextChangedListener(textWatcher)
}

fun TextView.setTextChangedListener(watcher: TextWatcher?) {
    clearWatchers()
    watcher?.let {
        addTextChangedListener(it)
        getWatchers().add(it)
    }
}

fun TextView.clearWatchers() {
    val watchers = getWatchers()
    watchers.forEach {
        removeTextChangedListener(it)
    }
    watchers.clear()
}

private fun TextView.getWatchers(): MutableList<TextWatcher> {
    @Suppress("UNCHECKED_CAST") return getTag(R.id.text_watchers) as? MutableList<TextWatcher>
        ?: run {
            val newList = mutableListOf<TextWatcher>()
            setTag(R.id.text_watchers, newList)
            newList
        }
}

inline fun makeTextWatcher(crossinline block: (CharSequence) -> Unit): TextWatcher =
    object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            block(s)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
    }