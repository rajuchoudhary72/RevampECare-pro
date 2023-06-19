package com.app.ecarepro.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter


@BindingAdapter(
    "leftWindowInsetToPadding",
    "topWindowInsetToPadding",
    "rightWindowInsetToPadding",
    "bottomWindowInsetToPadding",
    requireAll = false
)
fun View.addSystemWindowInsetToPadding(
    leftWindowInsetToPadding: Boolean = false,
    topWindowInsetToPadding: Boolean = false,
    rightWindowInsetToPadding: Boolean = false,
    bottomWindowInsetToPadding: Boolean = false,
) {


    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        view.updatePadding(
            left = if (leftWindowInsetToPadding) insets.left else 0,
            top = if (topWindowInsetToPadding) insets.top else 0,
            right = if (rightWindowInsetToPadding) insets.right else 0,
            bottom = if (bottomWindowInsetToPadding) insets.bottom else 0
        )

        WindowInsetsCompat.CONSUMED
    }
}

@BindingAdapter(
    "leftWindowInsetToMargin",
    "topWindowInsetToMargin",
    "rightWindowInsetToMargin",
    "bottomWindowInsetToMargin",
    requireAll = false
)
fun View.addSystemWindowInsetToMargin(
    leftWindowInsetToMargin: Boolean = false,
    topWindowInsetToMargin: Boolean = false,
    rightWindowInsetToMargin: Boolean = false,
    bottomWindowInsetToMargin: Boolean = false,
) {

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = if (leftWindowInsetToMargin) insets.left else 0,
                    top = if (topWindowInsetToMargin) insets.top else 0,
                    right = if (rightWindowInsetToMargin) insets.right else 0,
                    bottom = if (bottomWindowInsetToMargin) insets.bottom else 0
                )
            }
        }

        WindowInsetsCompat.CONSUMED
    }
}