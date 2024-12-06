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
            // Get the drawable (image) dimensions
            val drawable = drawable ?: return
            val imageWidth = drawable.intrinsicWidth
            val imageHeight = drawable.intrinsicHeight

            // Get the view (ZoomImageView) dimensions
            val viewWidth = width
            val viewHeight = height

            // Calculate the scale to fit the image into the view while maintaining aspect ratio
            val widthScale = viewWidth.toFloat() / imageWidth
            val heightScale = viewHeight.toFloat() / imageHeight
            val scale = widthScale.coerceAtMost(heightScale)

            // Scale the image
            matrix.setScale(scale, scale)

            // Center the image
            val offsetX = (viewWidth - imageWidth * scale) / 2f
            val offsetY = (viewHeight - imageHeight * scale) / 2f
            matrix.postTranslate(offsetX, offsetY)

            // Apply the matrix to the ImageView
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
