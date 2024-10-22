package com.app.ecarepro.ui.gallery.photo.photo_slider

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Video
import com.app.ecarepro.databinding.FragmentPhotoSliderBinding
import com.app.ecarepro.model.Photo
import com.app.ecarepro.model.photo_setting.AlbumSetting
import com.app.ecarepro.ui.gallery.videoPlay.YouTubeVideoPlayerFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.YoutubeURL
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.app.ecarepro.utils.shareImageFromUrl
import com.app.ecarepro.utils.shareUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class PhotoSliderFragment(
    val itemDat: Photo?,
    val itemVideo: Video?,
    val setting: AlbumSetting?,
    var galleryType: Int  = 1
) : Fragment() {


    private lateinit var binding : FragmentPhotoSliderBinding
    private val photoSliderViewModel : PhotoSliderViewModel by viewModels()

    private   var  likes: Int  = 0
    private   var  isFav: Boolean  = false
    private   var  isLike: Boolean  = false
    private var totalLikes = 0

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentPhotoSliderBinding.inflate(inflater,container,false)
         try {
              galleryType = requireArguments().getInt(Constant.GALLERY_TYPE)

         }catch (e:Exception){}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            if (galleryType==Constant.GALLERY_TYPE_PHOTO ){
                if (itemDat!=null){
                    isFav  = itemDat.isFavourite!!
                    isLike = itemDat.isLike!!
                    likes  = itemDat.likes!!

                    Picasso.get().load(itemDat.photoPath)
                        .placeholder(R.drawable.default_profile)
                        .into(binding.photoView)

                    binding.rlFav.setOnClickListener {
                        isFav = if (isFav ){
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites_blank, 0, 0)
                            photoSliderViewModel.manageFavorites(itemDat.id!!,galleryType,Constant.GALLERY_ACTION_REMOVE )
                            false

                        }else{
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)
                            photoSliderViewModel.manageFavorites(itemDat.id!!,galleryType,Constant.GALLERY_ACTION_ADD )
                            true
                        }

                    }

                    binding.llLike.setOnClickListener {
                        isLike = if (isLike ){
                            totalLikes -= 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_hover, 0, 0, 0);
                            photoSliderViewModel.manageLikes(itemDat.id!!,galleryType,false )
                            false
                        }else{
                            totalLikes += 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                            photoSliderViewModel.manageLikes(itemDat.id!!,galleryType,true )
                            true
                        }

                    }


                    binding.rlShare.setOnClickListener {
                        shareImageFromUrl(requireContext(), itemDat.photoPath.toString())
                    }

                }





            }else if (galleryType==Constant.GALLERY_TYPE_VIDEO ){
                if (itemVideo!=null){
                    isFav  = itemVideo.isFavourite!!
                    isLike = itemVideo.isLike!!
                    likes  = itemVideo.likes!!

                    binding.ivVideoPlay.isVisible=true

                    Picasso.get().load(YoutubeURL().getTIURLFromYoutubeURL(itemVideo.url))
                        .placeholder(R.drawable.default_profile)
                        .into(binding.photoView)

                    binding.rlShare.setOnClickListener {
                        shareUrl(requireContext(), itemVideo.url.toString())
                    }



                    binding.ivVideoPlay.setOnClickListener {
                        val id =  YoutubeURL().getIDFromYoutubeURL(itemVideo.url)
                        findNavController().navigate(
                            R.id.youTubeVideoPlayerFragment,
                            bundleOf(YouTubeVideoPlayerFragment.VIDEO_ID to YoutubeURL().getIDFromYoutubeURL(itemVideo.url))
                        )
                    }


                    binding.rlFav.setOnClickListener {
                        isFav = if (isFav ){
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites_blank, 0, 0)
                            photoSliderViewModel.manageFavorites(itemVideo.id!!,galleryType,Constant.GALLERY_ACTION_REMOVE )
                            false

                        }else{
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)
                            photoSliderViewModel.manageFavorites(itemVideo.id!!,galleryType,Constant.GALLERY_ACTION_ADD )
                            true
                        }

                    }

                    binding.llLike.setOnClickListener {
                        isLike = if (isLike ){
                            totalLikes -= 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_hover, 0, 0, 0);
                            photoSliderViewModel.manageLikes(itemVideo.id!!,galleryType,false )
                            false
                        }else{
                            totalLikes += 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                            photoSliderViewModel.manageLikes(itemVideo.id!!,galleryType,true )
                            true
                        }

                    }
                }
            }



        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

            if (setting!=null){
                binding.rlFav.isVisible= setting.isAddFavouriteEnabled!!
                binding.rlLikes.isVisible=setting.isLikeEnabled!!
                binding.rlShare.isVisible=setting.isShareEnabled !!
            }



         binding.tvNumberLike.text = "$likes Likes "
        totalLikes=likes





        if (isFav ){
            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)
        }
        if(isLike){
            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
        }





    }






}