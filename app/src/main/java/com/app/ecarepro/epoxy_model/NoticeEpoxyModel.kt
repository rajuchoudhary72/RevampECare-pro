package com.app.ecarepro.epoxy_model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class NoticeEpoxyModel(
    private val notices: Notice
) : ViewBindingKotlinModel<NoticeListItemBinding>(R.layout.notice_list_item) {
    override fun NoticeListItemBinding.bind() {

        tvTitleNotice.text = notices.heading
        noticeData = notices

        if (notices.isRead == false) {
            cvNotItem.maxCardElevation = 8F;
        }



        cvNotItem.setOnClickListener {

        }


    }

}