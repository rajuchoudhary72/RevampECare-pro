package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Timetable
import com.app.ecarepro.databinding.ItemTimetableCarouselCardBinding
import com.app.ecarepro.timetableCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class TimeTableCarouselModel(val data: List<Timetable>) :
    ViewBindingKotlinModel<ItemTimetableCarouselCardBinding>(R.layout.item_timetable_carousel_card) {
    override fun ItemTimetableCarouselCardBinding.bind() {
        carousel.withModels {
            data.forEach {
                timetableCard {
                    id(it.period)
                    timetable(it)
                }
            }
        }
    }
}