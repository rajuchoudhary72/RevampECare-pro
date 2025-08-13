package com.app.ecarepro.utils


import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

abstract class BaseFragment : Fragment() {

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)

        // Return a layout inflater backed by our localized context
        val localizedInflater = inflater.cloneInContext(LocalizedContext(requireContext()))

        // Install our custom layout inflater factory
        installFragmentLocalizedLayoutFactory(localizedInflater)

        return localizedInflater
    }

    // Install our custom factory for fragments
    private fun installFragmentLocalizedLayoutFactory(inflater: LayoutInflater) {
        try {
            // Find the activity to get its delegate
            val activity = requireActivity()
            if (activity is AppCompatActivity) {
                LayoutInflaterCompat.setFactory2(
                    inflater,
                    LocalizedLayoutInflaterFactory(activity.delegate)
                )
            }
        } catch (e: Exception) {
            // Factory already set or other error, can't override
        }
    }
}