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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

            Picasso.get()
                .load(data.photoPath)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .resize(100, 100)        // Resize to your ImageView size
                .centerCrop()            // Crop to fill the ImageView
                .noFade()                // Disable fade-in animation for speed
                .into(item.ivPhoto);

            item.ivPhoto.setOnClickListener {
                photoAlbumDTLFragment.onItemClick(albumList, pos = position,false)
            }


        }

    }


}