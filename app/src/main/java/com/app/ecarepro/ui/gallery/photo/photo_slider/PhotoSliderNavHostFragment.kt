package com.app.ecarepro.ui.gallery.photo.photo_slider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.databinding.FragmentPhotoSliderNavHostBinding
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.utils.Constant

class PhotoSliderNavHostFragment : Fragment() {

    private lateinit var binding : FragmentPhotoSliderNavHostBinding
    private lateinit var photoDetails: NetworkAlbumPhotoDetails
    private lateinit var networkVideoAlbumDTL: NetworkVideoAlbumDTL
    private var photoPosition =0
    private   var  galleryType: Int  = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentPhotoSliderNavHostBinding.inflate(inflater,container,false)
        try {
            photoPosition = requireArguments().getInt("photoPosition")
            galleryType = requireArguments().getInt(Constant.GALLERY_TYPE)
            }catch (e:Exception){ }

        try {
            photoDetails = requireArguments().getParcelable("photoDetails")!!
        } catch (e: Exception) { }

        try {
            networkVideoAlbumDTL = requireArguments().getParcelable("videoDetails")!!
        } catch (e: Exception) { }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (galleryType==Constant.GALLERY_TYPE_PHOTO){
            if (photoDetails!=null){
                if (photoDetails.photos!=null){

                    if (photoDetails.photos!!.isNotEmpty()){

                        val fragmentList : ArrayList<Fragment> = ArrayList()

                        photoDetails.photos!!. forEach { itemDat ->
                            fragmentList.add( PhotoSliderFragment(
                                itemDat,
                                null,
                                photoDetails.setting,
                                galleryType
                            ))
                        }

                        val viewPagerAdapter = ViewPagerAdapter(
                            fragmentList,
                            activity?.supportFragmentManager!!,
                            lifecycle
                        )
                        binding.viewPager.adapter = viewPagerAdapter
                        binding.viewPager.setCurrentItem(photoPosition, false)


                    }

                }}
        }else if (galleryType==Constant.GALLERY_TYPE_VIDEO){
            if (networkVideoAlbumDTL!=null){
                if (networkVideoAlbumDTL.videos!=null){

                    if (networkVideoAlbumDTL.videos .isNotEmpty()){

                        val fragmentList : ArrayList<Fragment> = ArrayList()

                        networkVideoAlbumDTL.videos . forEach { itemDat ->
                            fragmentList.add( PhotoSliderFragment(null,itemDat,networkVideoAlbumDTL.setting,galleryType ))
                        }

                        val viewPagerAdapter = ViewPagerAdapter(
                            fragmentList,
                            activity?.supportFragmentManager!!,
                            lifecycle
                        )
                        binding.viewPager.adapter = viewPagerAdapter
                        binding.viewPager.setCurrentItem(photoPosition, false)


                    }

                }}
        }


    }
}