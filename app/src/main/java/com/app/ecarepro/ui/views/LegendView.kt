package com.app.ecarepro.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LegendViewBinding

@SuppressLint("Recycle")
class LegendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: LegendViewBinding

    init {
        binding = LegendViewBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.LegendView, 0, 0)
            typedArray.getDrawable(R.styleable.LegendView_lv_image)?.let { drawable ->
                setImage(drawable)
            }
            setImageTint(typedArray.getColor(R.styleable.LegendView_lv_image_tint, Color.BLACK))

            typedArray.getString(R.styleable.LegendView_lv_text)?.let { title ->
                setTitle(title)
            }
            typedArray.getString(R.styleable.LegendView_lv_sub_text)?.let { subTitle ->
                setSubTitle(subTitle)
            }

            typedArray.recycle()
        }
    }

    fun setImageTint(color: Int) {
        binding.image.imageTintList = ColorStateList.valueOf(color)
    }

    fun setImage(drawable: Drawable) {
        binding.image.setImageDrawable(drawable)
    }

    fun setTitle(value: String) {
        binding.title.text = value
    }

    fun setSubTitle(value: String?) {
        binding.subTitle.text = value
    }
}
