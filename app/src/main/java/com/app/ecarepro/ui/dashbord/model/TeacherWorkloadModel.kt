package com.app.ecarepro.ui.dashbord.model

import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemTeacherWorkloadCardBinding
import com.app.ecarepro.teacherClassOverloadCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class TeacherWorkloadModel :
    ViewBindingKotlinModel<ItemTeacherWorkloadCardBinding>(R.layout.item_teacher_workload_card) {
    override fun ItemTeacherWorkloadCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.6f
        carousel.setPadding(Carousel.Padding(0, 15))
        carousel.withModels {
            (0..5).forEach {
                teacherClassOverloadCard {
                    id(it)
                }
            }
        }
    }

}