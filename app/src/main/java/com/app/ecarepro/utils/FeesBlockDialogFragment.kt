package com.app.ecarepro.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.ui.mainActivity

class FeesBlockDialogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.dialog_fees_block, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        view.findViewById<Button>(R.id.payButton).setOnClickListener {
            mainActivity().extracted()

        }
        view.findViewById<Button>(R.id.logout).setOnClickListener {
            findNavController().navigate(R.id.switchAccountFragment)
        }
    }
}
