package com.app.ecarepro.ui.gallery.photo.photoAlbum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemPhotoAlbumBinding
import com.app.ecarepro.model.Album
import com.app.ecarepro.model.LessonPlan
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PhotoAlbumAdapter(
    private var photoAlbumFragment: PhotoAlbumFragment
) :
    RecyclerView.Adapter<PhotoAlbumAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemPhotoAlbumBinding
    private var albumList = mutableListOf<Album>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemPhotoAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


 inner   class NoticeViewHolder(val item: ItemPhotoAlbumBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: Album) {
            item.tvName.text = data.title
            item.tvCollection.text = data.eventDate + " | " + data.totalPhotos + " Photos"

            Picasso.get().load(data.fileName)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.rlPhoto.setOnClickListener {
                try {
                    photoAlbumFragment.onItemClick(data,1,false)
                }catch (e:IllegalArgumentException){
                    e.printStackTrace()
                }

            }


        }

    }


}