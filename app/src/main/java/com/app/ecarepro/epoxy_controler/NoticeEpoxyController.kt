package com.app.ecarepro.epoxy_controler

import com.airbnb.epoxy.TypedEpoxyController
import com.app.ecarepro.epoxy_model.NoticeEpoxyModel
import com.app.ecarepro.model.Notice

class NoticeEpoxyController : TypedEpoxyController<List<Notice>>() {
    override fun buildModels(data: List<Notice>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        data.forEach {
            NoticeEpoxyModel(it).id(it.id).addTo(this)
        }
    }
}