package com.app.ecarepro.utils

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private val matrix = Matrix()

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        imageMatrix = matrix
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            // Center the image initially
            matrix.setTranslate((width - drawable.intrinsicWidth) / 2f, (height - drawable.intrinsicHeight) / 2f)
            imageMatrix = matrix
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Update scale factor with bounds for zoom level
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(1.0f, 5.0f) // Min and max zoom levels

            // Center the image while scaling
            matrix.postScale(
                detector.scaleFactor,
                detector.scaleFactor,
                width / 2f,
                height / 2f
            )

            // Apply the transformations
            imageMatrix = matrix
            return true
        }
    }
}
