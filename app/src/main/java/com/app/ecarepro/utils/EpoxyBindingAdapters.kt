package com.app.ecarepro.utils

import android.content.ContextWrapper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import com.app.ecarepro.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@BindingAdapter("inputText")
fun AutoCompleteTextView.setInputText(text: CharSequence?) {
    if (setText(this, text)) {
        // We already know the text differs, but find the last edit location and use
        // that as the selection.
        setSelection(this.length())
    }
}

@BindingAdapter("clickListener")
fun TextInputEditText.setClickListener(clickListener: View.OnClickListener?) {
    if (clickListener != null) {
        inputType = InputType.TYPE_NULL;
        keyListener = null
        setOnTouchListener { view, motionEvent ->
            if (motionEvent.getAction() === MotionEvent.ACTION_UP) {
                clickListener?.onClick(view)
            }
            false
        }
    }
}

@BindingAdapter("sampleItems", "itemSelectListener", requireAll = false)
fun AutoCompleteTextView.setSampleItems(
    items: List<String>?,
    itemSelectListener: ItemSelectListener?
) {
    if (items.isNullOrEmpty()) return
    val adapter = ArrayAdapter(context, R.layout.list_item, items)
    setAdapter(adapter)

    setOnItemClickListener { adapterView, view, i, l ->
        itemSelectListener?.onItemSelect(items[i])
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

interface ItemSelectListener {
    fun onItemSelect(item: String)
}

@BindingAdapter("datePicker")
fun TextInputEditText.setDatePicker(isDatePicker: Boolean) {
    //if(isDatePicker.not())return


    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(today)
            .build()

    datePicker.addOnPositiveButtonClickListener { selection ->
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = selection
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(calendar.time)
        Toast.makeText(context, formattedDate, Toast.LENGTH_LONG).show()
    }


    val fragmentManager = when (context) {
        is FragmentActivity -> (context as FragmentActivity).supportFragmentManager
        is ContextWrapper -> {
            val baseContext = (context as ContextWrapper).baseContext
            if (baseContext is FragmentActivity) {
                baseContext.supportFragmentManager
            } else {
                null
            }
        }

        else -> null
    }



    setOnFocusChangeListener { view, b ->
        if (b) {
            fragmentManager?.let {
                datePicker.show(it, "tag")
            }
        }
    }

}


@BindingAdapter("timePicker")
fun TextInputEditText.setTimePicker(isTimePicker: Boolean) {
    if (isTimePicker.not()) return

}