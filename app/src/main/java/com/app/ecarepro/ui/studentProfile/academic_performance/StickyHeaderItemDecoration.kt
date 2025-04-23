package com.app.ecarepro.ui.studentProfile.academic_performance

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class StickyHeaderItemDecoration(
    private val adapter: AcademicPerfListAdapter
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100

    private val headerPaint = Paint().apply {
        color = Color.parseColor("#E0F2F1") // Light green
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#00695C") // Dark green
        textSize = 36f
        isAntiAlias = true
        isFakeBoldText = true
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount

        var previousTitle: String? = null

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            val title = adapter.getSubject(position)

            if (adapter.isFirstInGroup(position)) {
                // Skip drawing header if this is the first visible item and already showing header
                if (i == 0 && child.top >= headerHeight) {
                    // First visible item already shows its own header
                    continue
                }

                val top = max(headerHeight, child.top)
                val bottom = top - headerHeight

                // Draw background
                c.drawRect(
                    left.toFloat(),
                    bottom.toFloat(),
                    right.toFloat(),
                    top.toFloat(),
                    headerPaint
                )

                // Draw header text
                c.drawText(
                    title,
                    (left + 40).toFloat(),
                    (top - 30).toFloat(),
                    textPaint
                )

                previousTitle = title
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (adapter.isFirstInGroup(position)) {
            outRect.top = headerHeight
        } else {
            outRect.top = 0
        }
    }
}
