package com.app.ecarepro.ui.gallery.kid_corner.kid_album_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentKidCornerDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.gallery.kid_corner.KidCornerShareViewModel
import com.app.ecarepro.ui.gallery.kid_corner.kid_corner_image_details.KidCornerSliderNavHostFragment
import com.app.ecarepro.ui.gallery.kid_corner.model.AlbumDetailX
import com.app.ecarepro.utils.Constant

import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KidAlbumDetailsFragment : Fragment(), ItemListener<AlbumDetailX> {


    private lateinit var kidCornerAdapter: KidAlbumDetailsAdapter
    private lateinit var binding: FragmentKidCornerDetailsBinding
    private val kidCornerViewModel: KidAlbumDetailsViewModel by viewModels()
    private val kidCornerShareViewModel: KidCornerShareViewModel by activityViewModels()
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private var albumID=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKidCornerDetailsBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack() }
        binding.toolbar.isVisible = true
        kidCornerAdapter = KidAlbumDetailsAdapter(this@KidAlbumDetailsFragment)

        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = kidCornerAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(KidId)?.let { id ->
            albumID=id
        }
        arguments?.getString(AlbumTitle)?.let { title ->
            binding.toolbar.title = title
        }

        lifecycleScope.launch {
            kidCornerViewModel.mediaGalleryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvPhotoAlbum.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvPhotoAlbum.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvPhotoAlbum.isVisible = true

                        if (it.data != null) {

                            if (it.data.albumDetails != null) {

                                if (pageIndex == 1) {
                                    kidCornerAdapter.clearData()
                                    kidCornerShareViewModel.clearList()
                                }

                                binding.tvCreatedOn.text=it.data.albumDetail.createdOn
                                binding.tvUpdatedOn.text=it.data.albumDetail.updatedOn
                                binding.tvYear.text=it.data.albumDetail.yearName
                                binding.tvDes.text=it.data.albumDetail.description

                                kidCornerViewModel.setAlbumDetail(it.data.albumDetail)


                                binding.rvPhotoAlbum.isVisible = true
                                binding.tvNoData.isVisible = false
                                isLoading = true

                                kidCornerViewModel.cacheListData.addAll(it.data.albumDetails)

                                kidCornerShareViewModel.setSelectedAlbum(it.data.albumDetails)
                                kidCornerAdapter.setData(it.data.albumDetails.toMutableList())

                            } else {
                                if (pageIndex == 1) {
                                    binding.rvPhotoAlbum.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }


        }


        setupRecycleViewPager()


        if (kidCornerViewModel.isFirst){
            kidCornerViewModel.getKidsAlbumDetails(
                pageIndex,
                albumID
            )
            kidCornerViewModel.isFirst=false
        }else{
            pageIndex= kidCornerViewModel.pageIndex
            kidCornerAdapter.setData(kidCornerViewModel.cacheListData)
            kidCornerViewModel.getAlbumDetail().value.apply {
                if (this != null) {
                    binding.tvCreatedOn.text=this.createdOn
                    binding.tvUpdatedOn.text=this.updatedOn
                    binding.tvYear.text=this.yearName
                    binding.tvDes.text=this.description
            }
            }
        }

    }


    private fun setupRecycleViewPager() {


        binding.rvPhotoAlbum.addOnScrollListener(object :
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
                                kidCornerViewModel.pageIndex=pageIndex
                                kidCornerViewModel.getKidsAlbumDetails(
                                    pageIndex,
                                    albumID
                                )
                            }
                        }

                    }
                }
            }
        })


    }

    override fun onItemClick(albumDetailX: AlbumDetailX, pos: Int, boolean: Boolean) {
        NavHostFragment.findNavController(this).navigate(
            R.id.kidCornerSliderNavHostFragment,
            Bundle().apply {
                putInt("photoPosition", pos)
            })
    }


    companion object {
        const val KidId = "id"
        const val AlbumTitle = "albumTitle"
    }

}