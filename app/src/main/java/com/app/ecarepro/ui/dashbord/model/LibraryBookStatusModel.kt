package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemLibraryBookStatusCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class LibraryBookStatusModel :
    ViewBindingKotlinModel<(ItemLibraryBookStatusCardBinding)>(R.layout.item_library_book_status_card) {
    private var isExpanded = false
    override fun ItemLibraryBookStatusCardBinding.bind() {
    }

}