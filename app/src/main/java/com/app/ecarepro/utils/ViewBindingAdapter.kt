package com.app.ecarepro.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.app.ecarepro.databinding.ItemCollectionBinding
import com.app.ecarepro.databinding.ItemCollectionCollectFooterBinding


@BindingAdapter("isVisible")
fun View.showOrGone(visible: Boolean) {
    isVisible = visible
}

@BindingAdapter("isInvisible")
fun View.showOrHide(invisible: Boolean) {
    isInvisible = invisible
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
fun LinearLayout.addCollectionItems(collections: Boolean) {
    removeAllViews()
    (0..7).forEach {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(context), null, false)
        binding.showDivider = it != 7
        addView(binding.root)

    }
    val footer = ItemCollectionCollectFooterBinding.inflate(LayoutInflater.from(context), null, false)
    addView(footer.root)
}