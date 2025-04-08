package com.app.ecarepro.ui.gallery.kid_corner.kid_corner_image_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.databinding.FragmentPhotoSliderNavHostBinding
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.gallery.kid_corner.KidCornerShareViewModel
import com.app.ecarepro.ui.gallery.kid_corner.kid_album_details.KidAlbumDetailsFragment.Companion.KidId
import com.app.ecarepro.utils.Constant

class KidCornerSliderNavHostFragment : Fragment() {

    private lateinit var binding : FragmentPhotoSliderNavHostBinding
    private var photoPosition =0
    private val kidCornerShareViewModel: KidCornerShareViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentPhotoSliderNavHostBinding.inflate(inflater,container,false)

        photoPosition = requireArguments().getInt("photoPosition")

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kidCornerShareViewModel.getSelectedAlbum().value.let { albumDetailXES ->

            val fragmentList : ArrayList<Fragment> = ArrayList()

            albumDetailXES?.forEach {
                fragmentList.add(KidCornerImageViewFragment.newInstance(it.fullImage,it.createdBy,it.name,it.description,it.guideBy,it.`class`))
            }

            val viewPagerAdapter = ViewPagerAdapter(
                fragmentList,
                activity?.supportFragmentManager!!,
                lifecycle
            )
            binding.viewPager.adapter = viewPagerAdapter
            binding.viewPager.setCurrentItem(photoPosition, false)
        }

    }


}