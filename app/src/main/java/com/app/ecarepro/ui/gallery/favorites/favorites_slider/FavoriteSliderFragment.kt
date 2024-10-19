package com.app.ecarepro.ui.gallery.favorites.favorites_slider

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import com.app.ecarepro.data.network.model.FavList
import com.app.ecarepro.databinding.FragmentPhotoSliderBinding
import com.app.ecarepro.ui.gallery.favorites.FavoritesViewModel
import com.app.ecarepro.ui.gallery.photo.photo_slider.PhotoSliderViewModel
import com.app.ecarepro.ui.gallery.videoPlay.YouTubeVideoPlayerFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.YoutubeURL
import com.app.ecarepro.utils.shareImageFromUrl
import com.app.ecarepro.utils.shareUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class FavoriteSliderFragment(
    val favList: FavList?,

    ) : Fragment() {


    private lateinit var binding : FragmentPhotoSliderBinding
    private val photoSliderViewModel : PhotoSliderViewModel by viewModels()
    private val favoritesViewModel : FavoritesViewModel by viewModels()
    private   var  likes: Int  = 0
    private   var  isFav: Boolean  = false
    private   var  isLike: Boolean  = false
    private var totalLikes = 0

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentPhotoSliderBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




                if (favList!=null){
                    isFav  = favList.isFavourite!!
                    isLike = favList.islLike==1
                    likes  = favList.totalLike!!



                     if (favList.galleryType==Constant.GALLERY_TYPE_PHOTO){
                         binding.ivVideoPlay.isVisible=false
                         Picasso.get().load(favList.fileName)
                             .placeholder(R.drawable.default_profile)
                             .into(binding.photoView)

                         binding.ivVideoPlay.setOnClickListener {
                             val id =  YoutubeURL().getIDFromYoutubeURL(favList.fileName)
                             findNavController().navigate(
                                 R.id.youTubeVideoPlayerFragment,
                                 bundleOf(YouTubeVideoPlayerFragment.VIDEO_ID to YoutubeURL().getIDFromYoutubeURL(favList.fileName))
                             )
                         }
                         binding.rlShare.setOnClickListener {
                             shareImageFromUrl(requireContext(), favList.fileName.toString())
                         }
                     }else{
                         binding.ivVideoPlay.isVisible=true
                         Picasso.get().load(YoutubeURL().getTIURLFromYoutubeURL(favList.fileName))
                             .placeholder(R.drawable.default_profile)
                             .into(binding.photoView)

                           binding.ivVideoPlay.setOnClickListener {
                             val id =  YoutubeURL().getIDFromYoutubeURL(favList.fileName)
                             findNavController().navigate(
                                 R.id.youTubeVideoPlayerFragment,
                                 bundleOf(YouTubeVideoPlayerFragment.VIDEO_ID to YoutubeURL().getIDFromYoutubeURL(favList.fileName))
                             )
                         }
                         binding.rlShare.setOnClickListener {
                             shareUrl(requireContext(), favList.fileName.toString())
                         }
                     }


                    binding.rlFav.setOnClickListener {
                        isFav = if (isFav ){
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites_blank, 0, 0)
                            photoSliderViewModel.manageFavorites(favList.id!!,
                                favList.galleryType!!,Constant.GALLERY_ACTION_REMOVE )
                            false

                        }else{
                            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)
                            photoSliderViewModel.manageFavorites(favList.id!!,
                                favList.galleryType!!,Constant.GALLERY_ACTION_ADD )
                            true
                        }
                        favoritesViewModel.getFavorites( 1)
                    }

                    binding.llLike.setOnClickListener {
                        isLike = if (isLike ){
                            totalLikes -= 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_hover, 0, 0, 0);
                            photoSliderViewModel.manageLikes(favList.id!!,favList.galleryType!!,false )
                            false
                        }else{
                            totalLikes += 1
                            binding.tvNumberLike.text="$totalLikes Likes "
                            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                            photoSliderViewModel.manageLikes(favList.id!!,favList.galleryType!!,true )
                            true
                        }

                    }


                }





        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }


                binding.rlFav.isVisible= true
                binding.rlLikes.isVisible=true
                binding.rlShare.isVisible=true




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