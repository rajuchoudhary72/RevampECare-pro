package com.app.ecarepro.ui.book_library.eBook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.EBookItemBinding
import com.app.ecarepro.databinding.LatestBookListItemBinding
import com.app.ecarepro.databinding.SearchBookListItemBinding
import com.app.ecarepro.model.Book
import com.app.ecarepro.ui.book_library.eBook.EBookFragment

import com.squareup.picasso.Picasso

class EBookAdapter(private var latestBookList: List<Book>,
                   private var eBookFragment: EBookFragment
) :
    RecyclerView.Adapter<EBookAdapter.ThoughtsViewHolder>() {

        private lateinit var bindingm:   EBookItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsViewHolder {
        bindingm=EBookItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ThoughtsViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = latestBookList.size

    override fun onBindViewHolder(holder: ThoughtsViewHolder, position: Int) {

        val bindings= DataBindingUtil.bind<EBookItemBinding>(holder.itemView)

         bindings?.apply {
             bindings.tvName.text=latestBookList[position].title
             bindings.tvAuthor.text= buildString {
                 append(eBookFragment.getString(R.string.general_author_pun))
                 append(latestBookList[position].author)
             }

                 bindingm.cvMain.setOnClickListener {
                     eBookFragment.onItemClick(latestBookList[position],1,true)
                 }

             Picasso.get().load(latestBookList[position].coverImg).
             placeholder(R.drawable.ic_library_big_image)
                 .into(bindingm.ivPhoto)

         }

    }

    class ThoughtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}