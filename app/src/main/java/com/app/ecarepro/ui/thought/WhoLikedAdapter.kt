package com.app.ecarepro.ui.thought

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.databinding.WhoLikedListItemBinding
import com.app.ecarepro.model.LikeBy
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class WhoLikedAdapter(private var likeByList : List<LikeBy> ) :
    RecyclerView.Adapter<WhoLikedAdapter.WholikedViewHolder>() {

        private lateinit var bindingm:   WhoLikedListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WholikedViewHolder {
        bindingm=WhoLikedListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WholikedViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = likeByList.size

    override fun onBindViewHolder(holder: WholikedViewHolder, position: Int) {

        val data= likeByList[position]
        bindingm.tvName.text=data.name

        Picasso.get().load(data.photo).
        placeholder(R.drawable.default_profile)
            .into(bindingm.userImg)

    }


    class WholikedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    }


}