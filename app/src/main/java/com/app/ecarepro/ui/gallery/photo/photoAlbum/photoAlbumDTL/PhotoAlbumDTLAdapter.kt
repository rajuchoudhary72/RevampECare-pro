package com.app.ecarepro.ui.gallery.photo.photoAlbum.photoAlbumDTL

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemPhotoAlbumBinding
import com.app.ecarepro.databinding.PhotoListItemBinding
import com.app.ecarepro.model.Album
import com.app.ecarepro.model.Photo
import com.squareup.picasso.Picasso

class PhotoAlbumDTLAdapter(
    private var photoAlbumDTLFragment: PhotoAlbumDTLFragment
) :
    RecyclerView.Adapter<PhotoAlbumDTLAdapter.NoticeViewHolder>() {

    private lateinit var bindingm:  PhotoListItemBinding
    private var albumList = mutableListOf<Photo>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = PhotoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data)


    }


    fun setData(mList: MutableList<Photo>) {
        albumList.addAll(mList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


   inner class NoticeViewHolder(val item: PhotoListItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: Photo) {


            Picasso.get().load(data.photoPath)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.ivPhoto.setOnClickListener {
                photoAlbumDTLFragment.onItemClick(data,absoluteAdapterPosition,false)
            }


        }

    }


}