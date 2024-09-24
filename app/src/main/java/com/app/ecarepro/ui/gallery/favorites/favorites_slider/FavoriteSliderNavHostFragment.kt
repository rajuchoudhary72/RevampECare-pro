package com.app.ecarepro.ui.gallery.favorites.favorites_slider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkFavorites
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.databinding.FragmentPhotoSliderNavHostBinding
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.gallery.photo.photo_slider.PhotoSliderFragment
import com.app.ecarepro.utils.Constant

class FavoriteSliderNavHostFragment : Fragment() {

    private lateinit var binding : FragmentPhotoSliderNavHostBinding
    private lateinit var networkFavorites: NetworkFavorites

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
            networkFavorites = requireArguments().getParcelable("favDetails")!!
        } catch (e: Exception) { }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

             if (networkFavorites!=null){
                if (networkFavorites.list!=null){

                    if (networkFavorites.list!!.isNotEmpty()){

                        val fragmentList : ArrayList<Fragment> = ArrayList()

                        networkFavorites.list!!. forEach { itemDat ->
                            fragmentList.add( FavoriteSliderFragment(
                                itemDat  )
                            )
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