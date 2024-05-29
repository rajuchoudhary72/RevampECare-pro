package com.app.ecarepro.ui.gallery.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.FavList
import com.app.ecarepro.databinding.ItemFavListPhotoVideoBinding
import com.app.ecarepro.databinding.ItemPhotoAlbumBinding
import com.app.ecarepro.utils.YoutubeURL
import com.squareup.picasso.Picasso

class FavoritesListAdapter(
    private var favoritesListFragment: FavoritesListFragment
) :
    RecyclerView.Adapter<FavoritesListAdapter.NoticeViewHolder>() {

    private lateinit var bindingm: ItemFavListPhotoVideoBinding
    private var albumList = mutableListOf<FavList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm = ItemFavListPhotoVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(bindingm)
    }

    override fun getItemCount(): Int = albumList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val data = albumList[position]
        holder.bind(data)


    }


    fun setData(lessonList: MutableList<FavList>) {
        albumList.addAll(lessonList)
        notifyDataSetChanged()
    }

    fun clearData() {
        albumList.clear()
        notifyDataSetChanged()
    }


 inner   class NoticeViewHolder(val item: ItemFavListPhotoVideoBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(data: FavList) {


            if (data.galleryType==1){
                Picasso.get().load(data.fileName)
                    .placeholder(R.drawable.default_profile)
                    .into(item.ivPhoto)
            }else{
                Picasso.get().load(YoutubeURL().getTIURLFromYoutubeURL(data.fileName))
                    .placeholder(R.drawable.default_profile)
                    .into(item.ivPhoto)
            }


            item.rlPhoto.setOnClickListener {
                favoritesListFragment.onItemClick(data,1,false)
            }


        }

    }


}