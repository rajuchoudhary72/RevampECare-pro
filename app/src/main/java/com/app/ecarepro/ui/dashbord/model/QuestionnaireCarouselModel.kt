package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Questionnaire
import com.app.ecarepro.databinding.ItemQuestionnaireCarouselCardBinding
import com.app.ecarepro.questionnaireCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class QuestionnaireCarouselModel(val data: List<Questionnaire>) :
    ViewBindingKotlinModel<ItemQuestionnaireCarouselCardBinding>(R.layout.item_questionnaire_carousel_card) {
    override fun ItemQuestionnaireCarouselCardBinding.bind() {
        carousel.numViewsToShowOnScreen = 1.2f
        carousel.withModels {
            data.forEach {
                questionnaireCard {
                    id(it.qid)
                    data(it)
                }
            }
        }
    }

}