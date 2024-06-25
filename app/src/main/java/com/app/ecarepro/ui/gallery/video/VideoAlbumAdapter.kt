package com.app.ecarepro.ui.gallery.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemPhotoAlbumBinding
import com.app.ecarepro.model.AlbumVideo
import com.squareup.picasso.Picasso

class VideoAlbumAdapter(
    private var videoAlbumFragment: VideoAlbumFragment
) :
    RecyclerView.Adapter<VideoAlbumAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemPhotoAlbumBinding
    private var albumList = mutableListOf<AlbumVideo>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemPhotoAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data)


    }


    fun setData(lessonList: MutableList<AlbumVideo>) {
        albumList.addAll(lessonList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


 inner   class NoticeViewHolder(val item: ItemPhotoAlbumBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: AlbumVideo) {
            item.tvName.text = data.title
            item.tvCollection.text = data.eventDate + " | " + data.totalVideos + " Video"

            Picasso.get().load(data.fileName)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.rlPhoto.setOnClickListener {
                videoAlbumFragment.onItemClick(data,1,false)
            }


        }

    }


}