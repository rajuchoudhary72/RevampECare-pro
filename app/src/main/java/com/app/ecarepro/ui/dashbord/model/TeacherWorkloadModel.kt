package com.app.ecarepro.ui.dashbord.model

import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Workload
import com.app.ecarepro.databinding.ItemRecentPhotoCarouselCardBinding
import com.app.ecarepro.databinding.ItemTeacherWorkloadCardBinding
import com.app.ecarepro.photoView
import com.app.ecarepro.teacherClassOverloadCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class TeacherWorkloadModel(val workload: List<Workload>) :
    ViewBindingKotlinModel<ItemTeacherWorkloadCardBinding>(R.layout.item_teacher_workload_card) {
    override fun ItemTeacherWorkloadCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.6f
        carousel.setPadding(Carousel.Padding(0,15))
        carousel.withModels {
            workload.forEach {
                teacherClassOverloadCard {
                    id(it.id)
                    workload(it)
                }
            }
        }
    }

}