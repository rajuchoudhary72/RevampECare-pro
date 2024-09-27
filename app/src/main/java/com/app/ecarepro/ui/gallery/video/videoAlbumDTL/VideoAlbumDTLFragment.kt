package com.app.ecarepro.ui.gallery.video.videoAlbumDTL

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.data.network.model.Video
import com.app.ecarepro.databinding.FragmentPhotoAlbumDTLBinding
import com.app.ecarepro.model.Photo
import com.app.ecarepro.model.photo_setting.AlbumSetting
import com.app.ecarepro.ui.MainActivity

import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.YoutubeURL
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoAlbumDTLFragment : Fragment() , ItemListener<Video> {

    private   var photoAlbumId: String = ""
    private lateinit var albumDTLAdapter: VideoAlbumDTLAdapter
    private lateinit var binding: FragmentPhotoAlbumDTLBinding
   private val videoAlbumDTLViewModel : VideoAlbumDTLViewModel by viewModels()
    private lateinit var albumSetting: AlbumSetting
    private lateinit var networkVideoAlbumDTL: NetworkVideoAlbumDTL
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPhotoAlbumDTLBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.title= "Videos"
        photoAlbumId = requireArguments().getString(Constant.ID).toString()
        albumDTLAdapter =    VideoAlbumDTLAdapter(this@VideoAlbumDTLFragment)
        binding.rvAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,4)
            adapter = albumDTLAdapter
        }


       return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvMore.setOnClickListener {
            binding.tvDes.setLines(binding.tvDes.lineCount)
            binding.tvMore.visibility = View.GONE
        }

        lifecycleScope.launch {
            videoAlbumDTLViewModel.photoAlbumStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAlbum.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAlbum.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAlbum.isVisible = true

                        if (it.data!=null){



                            if (it.data.videos!=null){

                                if (pageIndex==1){
                                    albumDTLAdapter.clearData()
                                }

                                binding.rvAlbum.isVisible=true
                                binding.tvNoAlbum.isVisible=false
                                isLoading=true

                                binding.tvHeading.text=it.data.title
                                binding.tvDes.text=it.data.description
                                binding.tvDatePhoto.text = it.data.eventDate + " | " + it.data.totalVideos + " Video"

                                if (binding.tvDes.getLineCount() >= 4) {
                                    binding.tvMore.setVisibility(View.VISIBLE)
                                } else {
                                    binding.tvMore.setVisibility(View.GONE)
                                }
                                albumSetting=it.data.setting
                                networkVideoAlbumDTL=it.data

                                albumDTLAdapter.setData(it.data.videos.toMutableList())

                            }else{
                                if (pageIndex==1){
                                    binding.rvAlbum.isVisible=false
                                    binding.tvNoAlbum.isVisible=true
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }


        }

        videoAlbumDTLViewModel.getVideoAlbumDTL( photoAlbumId,pageIndex)

        setupRecycleViewPager()


    }


    private fun setupRecycleViewPager() {
        binding.rvAlbum.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null) {
                    if (dy > 0) {
                        visibleItemCount = linearLayoutManager.childCount;
                        totalItemCount = linearLayoutManager.itemCount;
                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                        if (isLoading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                isLoading = false
                                pageIndex += 1
                                videoAlbumDTLViewModel.getVideoAlbumDTL( photoAlbumId,pageIndex)
                            }
                        }

                    }
                }
            }
        }) }

    override fun onItemClick(t: Video, pos: Int, boolean: Boolean) {
//        findNavController().navigate(R.id.action_videoAlbumDTLFragment_to_photoSliderFragment,
//            Bundle().apply {
//                putString(Constant.ID, t.id)
//                putString(Constant.URL_ARGUMENT, YoutubeURL().getTIURLFromYoutubeURL(t.url))
//                putString(Constant.FULL_URL_ARGUMENT, t.url)
//                putInt(Constant.GALLERY_TYPE, Constant.GALLERY_TYPE_VIDEO)
//                putBoolean("isLiked", t.isLike)
//                putBoolean("isFav", t.isFavourite)
//                putInt("likes", t.likes)
//                putBoolean("isLikeEnabled", albumSetting.isLikeEnabled!!)
//                putBoolean("isShareEnabled", albumSetting.isShareEnabled!!)
//                putBoolean("isAddFavouriteEnabled",albumSetting.isAddFavouriteEnabled!!)
//            })

        findNavController().navigate(R.id.photoSliderNavHostFragment ,
            Bundle().apply {
                putParcelable("videoDetails", networkVideoAlbumDTL)
                putInt("photoPosition", pos)
                putInt(Constant.GALLERY_TYPE, Constant.GALLERY_TYPE_VIDEO)
            })

    }

}