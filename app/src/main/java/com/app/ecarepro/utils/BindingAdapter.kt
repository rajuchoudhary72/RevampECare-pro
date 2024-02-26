package com.app.ecarepro.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.app.ecarepro.R
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String) {

    Picasso.get().load(url).
    placeholder(R.drawable.default_profile)
        .into(imageView)
}