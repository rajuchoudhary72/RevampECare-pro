package com.app.ecarepro.ui.gallery.mediaGallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Album
import com.app.ecarepro.data.network.model.FavList
import com.app.ecarepro.databinding.ItemFavListPhotoVideoBinding
import com.app.ecarepro.databinding.ItemMediaBinding
import com.app.ecarepro.utils.YoutubeURL
import com.squareup.picasso.Picasso

class MediaGalleryAdapter(
    private var mediaGalleryFragment: MediaGalleryFragment
) :
    RecyclerView.Adapter<MediaGalleryAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemMediaBinding
    private var albumList = mutableListOf<Album>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


 inner   class NoticeViewHolder(val item: ItemMediaBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: Album) {

            Picasso.get().load(data.fileName)
                .placeholder(R.drawable.default_profile)
                .into(item.ivPhoto)

            item.name.text=data.headline
            item.tvNewspaper.text=data.newsName
            item.tvPubliOn.text=data.publishedOn
            item.tvUpdtedOn.text=data.updatedOn



        }

    }


}