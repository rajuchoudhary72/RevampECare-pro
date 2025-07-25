package com.app.ecarepro.ui.gallery.kid_corner.kid_album_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemKidCornerDetailsItemBinding
import com.app.ecarepro.ui.gallery.kid_corner.model.AlbumDetailX
import com.squareup.picasso.Picasso

class KidAlbumDetailsAdapter(
    private var kidCornerFragment: KidAlbumDetailsFragment
) :
    RecyclerView.Adapter<KidAlbumDetailsAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemKidCornerDetailsItemBinding
    private var albumList = mutableListOf<AlbumDetailX>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemKidCornerDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data,position)


    }


    fun setData(lessonList: MutableList<AlbumDetailX>) {
        albumList.addAll(lessonList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


 inner   class NoticeViewHolder(val item: ItemKidCornerDetailsItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: AlbumDetailX, position: Int) {

            Picasso.get().load(data.thumbImage)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.cvMain.setOnClickListener {
                kidCornerFragment.onItemClick(data,position,false)
            }





        }

    }


}