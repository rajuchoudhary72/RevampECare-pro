package com.app.ecarepro.ui.dashbord.model

import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.StudentBirthDayCard
import com.app.ecarepro.databinding.ItemStudentBirthdayCardBinding
import com.app.ecarepro.studentBirthdayItemCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class StudentBirthdayCardModel(val workload: List<StudentBirthDayCard>, val onClick:(StudentBirthDayCard) -> Unit) :
    ViewBindingKotlinModel<ItemStudentBirthdayCardBinding>(R.layout.item_student_birthday_card) {
    override fun ItemStudentBirthdayCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.6f
        carousel.setPadding(Carousel.Padding(0,15))
        carousel.withModels {
            workload.forEach {
                studentBirthdayItemCard {
                    id(it.studentName+1)
                    workload(it)
                    clickListener { _ ->
                        onClick(it)
                    }
                }
            }
        }
    }

}