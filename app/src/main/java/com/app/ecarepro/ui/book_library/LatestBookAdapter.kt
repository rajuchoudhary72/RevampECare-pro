package com.app.ecarepro.ui.book_library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LatestBookListItemBinding
import com.app.ecarepro.model.LatestBook

import com.squareup.picasso.Picasso

class LatestBookAdapter(private var latestBookList: List<LatestBook>,
                        private var latestBookFragment: LatestBookFragment ) :
    RecyclerView.Adapter<LatestBookAdapter.ThoughtsViewHolder>() {

        private lateinit var bindingm:   LatestBookListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsViewHolder {
        bindingm=LatestBookListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ThoughtsViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = latestBookList.size

    override fun onBindViewHolder(holder: ThoughtsViewHolder, position: Int) {

        bindingm.latestBookData=latestBookList[position]


        bindingm.ivI.setOnClickListener {
            latestBookFragment.onItemClick(latestBookList[position],1,true)
        }

        Picasso.get().load(latestBookList[position].coverImg).
        placeholder(R.drawable.ic_library_big_image)
            .into(bindingm.userImg)

    }
    class ThoughtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}