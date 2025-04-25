// Add this class to your project

package com.app.ecarepro.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.app.ecarepro.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class LoadingProgressDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    private var progressBar: LinearProgressIndicator
    private var messageTextView: TextView
    private var countTextView: TextView
    private var maxProgress: Int = 100

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading_progress, null)
        dialog.setContentView(view)

        progressBar = view.findViewById(R.id.progressBar)
        messageTextView = view.findViewById(R.id.tvProgressMessage)
        countTextView = view.findViewById(R.id.tvProgressCount)
    }

    fun setMax(max: Int) {
        maxProgress = max
        progressBar.max = max
    }

    fun setProgress(progress: Int) {
        progressBar.progress = progress
        countTextView.text = "$progress/$maxProgress"
    }

    fun setMessage(message: String) {
        messageTextView.text = message
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}