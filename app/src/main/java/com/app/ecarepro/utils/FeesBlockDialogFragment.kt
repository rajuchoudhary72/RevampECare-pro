package com.app.ecarepro.utils

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.app.ecarepro.R
import com.app.ecarepro.ui.mainActivity

class FeesBlockDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        isCancelable = false // Don't allow user to dismiss
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.dialog_fees_block, container, false)
    }
    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.payButton).setOnClickListener {
            // TODO: Navigate to payment screen or open WebView
            dismiss() // Optional: remove if user must pay
            mainActivity().extracted()

        }
    }
}
