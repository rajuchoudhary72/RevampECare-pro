package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemRecentPhotoCarouselCardBinding
import com.app.ecarepro.photoView
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class RecentPhotoCarouselModel :
    ViewBindingKotlinModel<ItemRecentPhotoCarouselCardBinding>(R.layout.item_recent_photo_carousel_card) {
    override fun ItemRecentPhotoCarouselCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.3f
        carousel.withModels {
            (0..5).forEach {
                photoView {
                    id(it)
                }

            }
        }
    }

}