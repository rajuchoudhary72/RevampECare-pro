package com.app.ecarepro.ui.dashbord.model

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.dashboardCardSmall
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.Timetable
import com.app.ecarepro.databinding.ItemProcardCarouselCardBinding
import com.app.ecarepro.timetableCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class ProCardCarouselModel(val data: List<Card>, val clickListener:(Card) -> Unit) :
    ViewBindingKotlinModel<ItemProcardCarouselCardBinding>(R.layout.item_procard_carousel_card) {
    private var isHorizontal = true
    override fun ItemProcardCarouselCardBinding.bind() {
        carousel.withModels {
            data.forEach {
                dashboardCardSmall {
                    id(it.menuID)
                    card(it)
                    clickListener { _ ->
                        clickListener(it)
                    }

            }
        }

        imageArrow.setOnClickListener {
            if (isHorizontal) {
                isHorizontal = false
                imageArrow.rotation = 180f
                carousel.layoutManager = GridLayoutManager(root.context, 3)
            } else {
                isHorizontal = true
                imageArrow.rotation = 0f
                carousel.layoutManager =
                    LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
            }

            carousel.requestModelBuild()
        }
    }
}}