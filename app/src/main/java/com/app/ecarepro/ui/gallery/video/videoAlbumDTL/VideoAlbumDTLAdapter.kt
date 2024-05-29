package com.app.ecarepro.ui.gallery.video.videoAlbumDTL

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Video
import com.app.ecarepro.databinding.PhotoListItemBinding
import com.app.ecarepro.utils.YoutubeURL
import com.squareup.picasso.Picasso

class VideoAlbumDTLAdapter(
    private var videoAlbumDTLFragment: VideoAlbumDTLFragment
) :
    RecyclerView.Adapter<VideoAlbumDTLAdapter.NoticeViewHolder>() {

    private lateinit var bindingm:  PhotoListItemBinding
    private var albumList = mutableListOf<Video>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = PhotoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data)


    }


    fun setData(mList: MutableList<Video>) {
        albumList.addAll(mList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


  inner  class NoticeViewHolder(val item: PhotoListItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: Video) {


            Picasso.get().load(YoutubeURL().getTIURLFromYoutubeURL(data.url))
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.ivVideoPlay.isVisible=true

            item.ivPhoto.setOnClickListener {
                videoAlbumDTLFragment.onItemClick(data,1,false)
            }


        }

    }


}