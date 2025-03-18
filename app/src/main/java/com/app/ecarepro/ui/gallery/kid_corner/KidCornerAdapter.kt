package com.app.ecarepro.ui.gallery.kid_corner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemGallerymoduleBinding
import com.app.ecarepro.databinding.ItemKidCornerItemLayoutBinding
import com.app.ecarepro.ui.gallery.kid_corner.model.Album
import com.squareup.picasso.Picasso

class KidCornerAdapter(
    private var kidCornerFragment: KidCornerFragment
) :
    RecyclerView.Adapter<KidCornerAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemKidCornerItemLayoutBinding
    private var albumList = mutableListOf<Album>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemKidCornerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data)


    }


    fun setData(lessonList: MutableList<Album>) {
        albumList.addAll(lessonList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


 inner   class NoticeViewHolder(val item: ItemKidCornerItemLayoutBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: Album) {

            Picasso.get().load(data.albumIcon)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.tvTitle.text=data.title
            item.tvEventOn.text= kidCornerFragment.getString(R.string.event_on)+data.createdOn
            item.tvTotalPhotos.text= kidCornerFragment.getString(R.string.total_memories)+data.totalPhoto
            item.cvMain.setOnClickListener {
                kidCornerFragment.onItemClick(data,0,false)
            }





        }

    }


}