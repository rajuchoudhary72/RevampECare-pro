package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemRecentPhotoCarouselCardBinding
import com.app.ecarepro.databinding.ItemStaffAttendanceCardBinding
import com.app.ecarepro.photoView
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class RecentPhotoCarouselModel :
    ViewBindingKotlinModel<ItemRecentPhotoCarouselCardBinding>(R.layout.item_recent_photo_carousel_card) {
    override fun ItemRecentPhotoCarouselCardBinding.bind() {
            carousel.withModels {
                carouselNoSnapBuilder {
                    id("carousel")
                    numViewsToShowOnScreen(1.8f)
                    (0..5).forEach {
                        photoView {
                            id(it)
                        }
                    }
                }
            }
    }

}