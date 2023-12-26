package com.app.ecarepro.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Slide
import androidx.transition.TransitionManager

inline fun <V : View> V.afterMeasured(crossinline f: V.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

inline fun View?.onSizeChange(crossinline runnable: () -> Unit) = this?.apply {
    addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val rect = Rect(left, top, right, bottom)
        val oldRect = Rect(oldLeft, oldTop, oldRight, oldBottom)
        if (rect.width() != oldRect.width() || rect.height() != oldRect.height()) {
            runnable()
        }
    }
}

fun View.animateVisibility(setVisible: Boolean) {
    if (setVisible) expand(this) else collapse(this)
}

private fun expand(view: View) {
    view.measure(
        View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val initialHeight = 0
    val targetHeight = view.measuredHeight

    // Older versions of Android (pre API 21) cancel animations for views with a height of 0.
    //v.getLayoutParams().height = 1;
    view.layoutParams.height = 0
    view.visibility = View.VISIBLE

    animateView(view, initialHeight, targetHeight)
}

private fun collapse(view: View) {
    val initialHeight = view.measuredHeight
    val targetHeight = 0

    animateView(view, initialHeight, targetHeight)
}

private fun animateView(v: View, initialHeight: Int, targetHeight: Int) {
    val valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight)
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator) {
            v.layoutParams.height = targetHeight
        }

        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    valueAnimator.duration = 300
    valueAnimator.interpolator = FastOutSlowInInterpolator()
    valueAnimator.start()
}


fun View.slideVisibility(visibility: Boolean, durationTime: Long = 300) {
    val transition = Slide(Gravity.BOTTOM)
    transition.apply {
        duration = durationTime
        addTarget(this@slideVisibility)
    }
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.isVisible = visibility
}

//Source: source: https://guides.codepath.com/android/circular-reveal-animation
fun View.circularReveal() {

    fun doIt() {
        // get the center for the clipping circle
        val cx = measuredWidth / 2
        val cy = measuredHeight / 2

        // get the final radius for the clipping circle
        val finalRadius = width.coerceAtLeast(height) / 2

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius.toFloat())

        // make the view visible and start the animation
        visibility = View.VISIBLE
        anim.start()
    }
    if (visibility == View.GONE) {
        visibility = View.INVISIBLE
        afterMeasured {
            doIt()
        }
    } else {
        doIt()
    }
}

fun View.circularHide() {

    // get the center for the clipping circle
    val cx = measuredWidth / 2
    val cy = measuredHeight / 2

    // get the final radius for the clipping circle
    val initialRadius = width / 2

    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), 0f)

    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            visibility = View.GONE
        }
    })

    anim.start()
}