package com.app.ecarepro.ui.book_library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.SearchBookListItemBinding
import com.app.ecarepro.model.BookDTL
import com.squareup.picasso.Picasso

class SearchBookAdapter(private var latestBookList: List<BookDTL>,
                        private var librarySearchFragment: LibrarySearchFragment  ) :
    RecyclerView.Adapter<SearchBookAdapter.ThoughtsViewHolder>() {

        private lateinit var bindingm:   SearchBookListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsViewHolder {
        bindingm=SearchBookListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ThoughtsViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = latestBookList.size

    override fun onBindViewHolder(holder: ThoughtsViewHolder, position: Int) {

        val bindings= DataBindingUtil.bind<SearchBookListItemBinding>(holder.itemView)

        bindings!!.latestBookData=latestBookList[position]


        bindings.ivI.setOnClickListener {
            librarySearchFragment.onItemClick(latestBookList[position],1,true)
        }

        Picasso.get().load(latestBookList[position].coverImg).
        placeholder(R.drawable.ic_library_big_image)
            .into(bindings.userImg)


    }

    class ThoughtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}