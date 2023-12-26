package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemLibraryBookStatusCardBinding
import com.app.ecarepro.databinding.ItemSubscriberCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class SubscriberModel :
    ViewBindingKotlinModel<(ItemSubscriberCardBinding)>(R.layout.item_subscriber_card) {
    private var isExpanded = false
    override fun ItemSubscriberCardBinding.bind() {
    }

}