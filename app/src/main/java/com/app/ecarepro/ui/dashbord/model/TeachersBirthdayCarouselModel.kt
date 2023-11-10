package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemRecentPhotoCarouselCardBinding
import com.app.ecarepro.databinding.ItemTeachersBirthdayCarouselCardBinding
import com.app.ecarepro.photoView
import com.app.ecarepro.teacherBirthdayCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class TeachersBirthdayCarouselModel :
    ViewBindingKotlinModel<ItemTeachersBirthdayCarouselCardBinding>(R.layout.item_teachers_birthday_carousel_card) {
    override fun ItemTeachersBirthdayCarouselCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.8f
        carousel.withModels {
            (0..5).forEach {
                teacherBirthdayCard{
                    id(it)
                }

            }
        }
    }

}