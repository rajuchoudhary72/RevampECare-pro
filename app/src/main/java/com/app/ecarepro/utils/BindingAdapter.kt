package com.app.ecarepro.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.app.ecarepro.R
import com.squareup.picasso.Picasso
import com.google.android.material.card.MaterialCardView
import androidx.core.content.ContextCompat
import android.graphics.Color

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String) {

    Picasso.get().load(url).
    placeholder(R.drawable.default_profile)
        .into(imageView)
}

@BindingAdapter("cardBgColor")
fun MaterialCardView.cardBackgroundColor(colorCode: String?) {
    setCardBackgroundColor(
        if (colorCode.isNullOrEmpty()) ContextCompat.getColor(
            context,
            R.color.category7
        ) else Color.parseColor(colorCode)
    )
}