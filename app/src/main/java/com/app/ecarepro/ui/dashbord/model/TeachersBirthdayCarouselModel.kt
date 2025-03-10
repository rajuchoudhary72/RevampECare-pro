package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.BirthDayCard
import com.app.ecarepro.databinding.ItemTeachersBirthdayCarouselCardBinding
import com.app.ecarepro.teacherBirthdayCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import androidx.recyclerview.widget.GridLayoutManager


class TeachersBirthdayCarouselModel(
    val data: List<BirthDayCard>,
    val onClick: (BirthDayCard) -> Unit
) :
    ViewBindingKotlinModel<ItemTeachersBirthdayCarouselCardBinding>(R.layout.item_teachers_birthday_carousel_card) {
    override fun ItemTeachersBirthdayCarouselCardBinding.bind() {
        carousel.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(
                GridMarginDecoration.create(
                    margin = context.resources.getDimensionPixelSize(R.dimen.ed_around_5),
                    columnProvider = object : ColumnProvider {
                        override fun getNumberOfColumns(): Int {
                            return 2
                        }
                    }
                ))
        }
        carousel.withModels {
            data.forEach {
                teacherBirthdayCard{
                    id(it.heading)
                    data(it)
                    clickListener{_ -> onClick(it)}
                }

            }
        }
    }

}