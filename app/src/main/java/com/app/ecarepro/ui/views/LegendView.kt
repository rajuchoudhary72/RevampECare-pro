package com.app.ecarepro.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemLegendViewBinding

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
@SuppressLint("Recycle")
class LegendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ItemLegendViewBinding

    init {
        binding = ItemLegendViewBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.LegendView, 0, 0)
            typedArray.getDrawable(R.styleable.LegendView_lv_image).let { drawable ->
                setImage(drawable)
            }
            setImageTint(typedArray.getColor(R.styleable.LegendView_lv_image_tint, -1))

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
        if (color != -1)
            binding.image.imageTintList = ColorStateList.valueOf(color)
    }

    @ModelProp(ModelProp.Option.DoNotHash)
    fun setImage(drawable: Drawable?) {
        binding.image.isVisible = drawable != null
        binding.image.setImageDrawable(drawable)
    }

    @ModelProp
    fun setTitle(value: String) {
        binding.title.text = value
    }

    @ModelProp
    fun setSubTitle(value: String?) {
        binding.subTitle.text = value
    }
}

@BindingAdapter("subTitle")
fun LegendView.subTitle(subTitle: String?) {
    setSubTitle(subTitle)
}