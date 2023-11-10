package com.app.ecarepro.ui.dashbord.model

import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemFeedsCardBinding
import com.app.ecarepro.notificationCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class FeedsModel :
    ViewBindingKotlinModel<ItemFeedsCardBinding>(R.layout.item_feeds_card) {
    override fun ItemFeedsCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1f
        carousel.setPadding(Carousel.Padding(0, 15))
        carousel.withModels {
            (0..5).forEach {
                notificationCard {
                    id(it)
                }
            }
        }
    }

}