package com.app.ecarepro.ui.gallery.photo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentPhotoAlbumBinding
import com.app.ecarepro.databinding.FragmentPhotoAlbumTypeNavHostBinding
import com.app.ecarepro.model.Album
import com.app.ecarepro.model.AlbumType
import com.app.ecarepro.model.Photo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.gallery.photo.photoAlbum.PhotoAlbumFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhotoAlbumTypeNavHostFragment : Fragment() {

    private lateinit var binding: FragmentPhotoAlbumTypeNavHostBinding
    private val photoAlbumViewModel : PhotoAlbumTypeNavHostViewModel by viewModels()
    val albumList=   mutableListOf<AlbumType>()
    private var isDataLoaded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPhotoAlbumTypeNavHostBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            photoAlbumViewModel.photoAlbumTypesStateFlow.collectLatest {

                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            if (it.data !=null ) {

                                albumList.clear()
                                  albumList.addAll(it .data.albumTypes)
                                albumList.add(0,AlbumType( 0, "All"))

                                val fragmentList : ArrayList<Fragment> = ArrayList()
                                 albumList.forEach { albumType ->
                                    fragmentList.add(PhotoAlbumFragment.newInstance(albumType))
                                }



                                val viewPagerAdapter = ViewPagerAdapter(
                                    fragmentList,
                                    activity?.supportFragmentManager!!,
                                    lifecycle
                                )
                                binding.viewPager.adapter = viewPagerAdapter


                                TabLayoutMediator(
                                    binding.tabLayout,
                                    binding.viewPager
                                ) { tab, position ->

                                  //  tab.text="All"
                                    tab.text=  albumList[position].typeName

                                }.attach()
                                isDataLoaded=true

                            }

                        }

                    }

                    else -> {}
                }


            }
        }

       if (!isDataLoaded){
           photoAlbumViewModel.getPhotoAlbumTypes()
       }


    }



}