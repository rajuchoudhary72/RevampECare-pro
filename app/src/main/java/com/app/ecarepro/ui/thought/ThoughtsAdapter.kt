package com.app.ecarepro.ui.thought

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ThoughtsListItemBinding
import com.app.ecarepro.model.Thoughts
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ThoughtsAdapter(
    private var thoughtsList: List<Thoughts>,
    private var thoughtsListFragment: ThoughtsListFragment
) :
    RecyclerView.Adapter<ThoughtsAdapter.ThoughtsViewHolder>() {

    private lateinit var bindingm: ThoughtsListItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsViewHolder {
        bindingm =
            ThoughtsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThoughtsViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = thoughtsList.size

    override fun onBindViewHolder(holder: ThoughtsViewHolder, position: Int) {


        val data = thoughtsList[position]

        var like = data.isILike == 0

        setLikeDisLikeUi(like, holder)

        holder.word.text = data.quotation
        holder.tv_aut.text = data.author
        holder.updated_by_person.text = data.updatedBy
        holder.total_like.text = data.likes.toString() + " Likes"

        Picasso.get().load(thoughtsList[position].photo).placeholder(R.drawable.default_profile)
            .into(holder.user_img)

        if (!data.isVerified) {
            holder.rl_likes.isVisible = false
            holder.tv_thoughtStatus.isVisible = true
            holder.tv_thoughtStatus.text = "Pending"
        }

        var likeCount = data.likes


        holder.unlike.setOnClickListener {
            if (like) {
                likeCount += 1
                thoughtsListFragment.onItemClick(data, 1, like)
                like = false

            } else {
                likeCount -= 1
                thoughtsListFragment.onItemClick(data, 1, like)
                like = true
            }
            setLikeDisLikeUi(like, holder)
            holder.total_like.text = "$likeCount Likes"
        }

        holder.like.setOnClickListener {
            if (like) {
                likeCount += 1
                thoughtsListFragment.onItemClick(data, 1, like)
                like = false

            } else {
                likeCount -= 1
                thoughtsListFragment.onItemClick(data, 1, like)
                like = true

            }
            setLikeDisLikeUi(like, holder)
            holder.total_like.text = "$likeCount Likes"
        }

        holder.total_like.setOnClickListener {
            if (data.likes > 0) {
                thoughtsListFragment.onItemClick(data, 2, true)
            }
        }

        holder.rel_dot.setOnClickListener {
            thoughtsListFragment.onItemClick(data, 3, true)
        }


    }


    private fun setLikeDisLikeUi(boolean: Boolean, holder: ThoughtsViewHolder) {
        if (boolean) {
            holder.unlike.isVisible = true
            holder.like.isVisible = false
        } else {
            holder.unlike.isVisible = false
            holder.like.isVisible = true
        }
    }


    class ThoughtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val unlike: TextView = itemView.findViewById(R.id.unkike)
        val like: TextView = itemView.findViewById(R.id.like)
        val total_like: TextView = itemView.findViewById(R.id.total_like)
        val word: TextView = itemView.findViewById(R.id.word)
        val tv_aut: TextView = itemView.findViewById(R.id.tv_aut)
        val updated_by_person: TextView = itemView.findViewById(R.id.updated_by_person)
        val rl_likes: LinearLayout = itemView.findViewById(R.id.rl_likes)
        val tv_thoughtStatus: TextView = itemView.findViewById(R.id.tv_thoughtStatus)
        val user_img: ShapeableImageView = itemView.findViewById(R.id.user_img)
        val rel_dot: RelativeLayout = itemView.findViewById(R.id.rel_dot)

    }

    fun setData(thoughtsList: List<Thoughts>) {
        this.thoughtsList = thoughtsList as ArrayList<Thoughts>
        notifyDataSetChanged()
    }
}