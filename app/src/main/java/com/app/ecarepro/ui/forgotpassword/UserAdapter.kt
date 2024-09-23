package com.app.ecarepro.ui.forgotpassword


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.UserData
import com.app.ecarepro.databinding.UserItemBinding
import com.squareup.picasso.Picasso


class UserAdapter(
    private val itemList: MutableList<UserData>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserItemViewHolder>() {
    var selected = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mainBinding: UserItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.user_item, parent, false)

        return UserItemViewHolder(mainBinding.root)
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.binding?.let {
            Picasso.get().load(item.photo)
                .placeholder(R.drawable.ic_profile)
                .into(it.ivPofile)

     //       Picasso.get().load(item.photo).into(it.ivPofile)
            it.tvName.text = item.memberName
            it.tvClass.text = item.`class`
            if (selected == position) {
                it.ivCheck.visibility = View.VISIBLE
            } else {
                it.ivCheck.visibility = View.GONE
            }
            it.root.setOnClickListener {
                selected = position
                notifyDataSetChanged()
            }
        }


    }

    override fun getItemCount() = itemList.size

    class UserItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: UserItemBinding? = DataBindingUtil.bind(itemView)
    }
}