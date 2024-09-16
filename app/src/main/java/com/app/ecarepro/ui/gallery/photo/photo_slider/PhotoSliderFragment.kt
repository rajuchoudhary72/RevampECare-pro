package com.app.ecarepro.ui.gallery.photo.photo_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentPhotoSliderBinding
import com.app.ecarepro.ui.gallery.videoPlay.YouTubeVideoPlayerFragment
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.YoutubeURL
import com.app.ecarepro.utils.imageUrl
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PhotoSliderFragment : Fragment() {


    private lateinit var binding : FragmentPhotoSliderBinding
    private val photoSliderViewModel : PhotoSliderViewModel by viewModels()
    private   var  Id: String = ""
    private   var  url: String = ""
    private   var  fullUrl: String = ""

    private   var  galleryType: Int  = 1
    private   var  isLike: Boolean  = false
    private   var  isFav: Boolean  = false

    private   var  isAddFavouriteEnabled: Boolean  = false
    private   var  isShareEnabled: Boolean  = false
    private   var  isLikeEnabled: Boolean  = false

    private   var  likes: Int  = 0
    private var totalLikes = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentPhotoSliderBinding.inflate(inflater,container,false)
        Id = requireArguments().getString(Constant.ID).toString()
        url = requireArguments().getString(Constant.URL_ARGUMENT).toString()
        fullUrl = requireArguments().getString(Constant.FULL_URL_ARGUMENT).toString()
        galleryType = requireArguments().getInt(Constant.GALLERY_TYPE)
        isLike = requireArguments().getBoolean("isLiked")
        isFav = requireArguments().getBoolean("isFav")
        likes = requireArguments().getInt("likes")

        isAddFavouriteEnabled = requireArguments().getBoolean("isAddFavouriteEnabled")
        isShareEnabled = requireArguments().getBoolean("isShareEnabled")
        isLikeEnabled = requireArguments().getBoolean("isLikeEnabled")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.rlFav.isVisible=isAddFavouriteEnabled
        binding.rlLikes.isVisible=isLikeEnabled
        binding.rlShare.isVisible=isShareEnabled

         binding.tvNumberLike.text = "$likes Likes "
        totalLikes=likes

        if (galleryType==Constant.GALLERY_TYPE_VIDEO ){
            binding.ivVideoPlay.isVisible=true
            Picasso.get().load(url)
                .placeholder(R.drawable.default_profile)
                .into(binding.photoView)
        }else{
            Picasso.get().load(fullUrl)
                .placeholder(R.drawable.default_profile)
                .into(binding.photoView)
        }

        binding.ivVideoPlay.setOnClickListener {
            val id =  YoutubeURL().getIDFromYoutubeURL(fullUrl)
            findNavController().navigate(
                    R.id.youTubeVideoPlayerFragment,
            bundleOf(YouTubeVideoPlayerFragment.VIDEO_ID to YoutubeURL().getIDFromYoutubeURL(fullUrl))
            )
        }

        if (isFav){
            binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)

        }
        if(isLike){
            binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
        }





        binding.rlFav.setOnClickListener {
            isFav = if (isFav){
                binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites_blank, 0, 0)
                photoSliderViewModel.manageFavorites(Id,galleryType,Constant.GALLERY_ACTION_REMOVE )
                false

            }else{
                binding.tvFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_to_favourites, 0, 0)
                photoSliderViewModel.manageFavorites(Id,galleryType,Constant.GALLERY_ACTION_ADD )
                true
            }

            }

        binding.llLike.setOnClickListener {
            isLike = if (isLike){
                totalLikes -= 1
                binding.tvNumberLike.text="$totalLikes Likes "
                binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_hover, 0, 0, 0);
                photoSliderViewModel.manageLikes(Id,galleryType,false )
                false
            }else{

                totalLikes += 1
                binding.tvNumberLike.text="$totalLikes Likes "
                binding.tvLikeimage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                photoSliderViewModel.manageLikes(Id,galleryType,true )
                true
            }

        }






    }



}