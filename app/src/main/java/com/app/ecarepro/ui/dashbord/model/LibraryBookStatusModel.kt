package com.app.ecarepro.ui.dashbord.model

import android.content.Context
import android.view.LayoutInflater
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.LibraryDetails
import com.app.ecarepro.databinding.ItemLibraryBookStatusCardBinding
import com.app.ecarepro.databinding.ItemLibraryBooksBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel

class LibraryBookStatusModel(val data: LibraryDetails) :
    ViewBindingKotlinModel<(ItemLibraryBookStatusCardBinding)>(R.layout.item_library_book_status_card) {
    override fun ItemLibraryBookStatusCardBinding.bind() {
        books.removeAllViews()
        books.apply {
            removeAllViews()
            addView(getView(context).also {
                it.setTitle("Total Books")
                it.setData(data.totalBooks.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("Circulated Books")
                it.setData(data.circulatedBooks.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("Discarded Books")
                it.setData(data.discardedBooks.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("Due Fine")
                it.setData(data.dueFine.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("Fine Collected")
                it.setData(data.fineCollected.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("Magazine Subscribed")
                it.setData(data.magzineSubscribed.toString())
            }.root)
            addView(getView(context).also {
                it.setTitle("News Subscribed")
                it.setData(data.newsSubscribed.toString())
            }.root)
        }
    }

    fun getView(context: Context) =
        ItemLibraryBooksBinding.inflate(LayoutInflater.from(context), null, false)

}