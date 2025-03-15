package com.app.ecarepro.ui.gallery.kid_corner.kid_album_details

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentKidCornerBinding
import com.app.ecarepro.databinding.FragmentKidCornerDetailsBinding
import com.app.ecarepro.databinding.FragmentMediaGalleryBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.gallery.kid_corner.kid_corner_image_details.KidCornerImageViewFragment
import com.app.ecarepro.ui.gallery.kid_corner.model.Album
import com.app.ecarepro.ui.gallery.kid_corner.model.AlbumDetailX
import com.app.ecarepro.ui.gallery.mediaGallery.adapter.SearchByPopUpAdapter
import com.app.ecarepro.ui.gallery.mediaGallery.mediaDetails.MediaDetailsFragment
import com.app.ecarepro.ui.gallery.mediaGallery.mediaDetails.MediaDetailsFragment.Companion.description

import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

@AndroidEntryPoint
class KidAlbumDetailsFragment : Fragment(), ItemListener<AlbumDetailX> {


    private lateinit var kidCornerAdapter: KidAlbumDetailsAdapter
    private lateinit var binding: FragmentKidCornerDetailsBinding
    private val kidCornerViewModel: KidAlbumDetailsViewModel by viewModels()

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
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
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
                                }

                                binding.tvCreatedOn.text=it.data.albumDetail.createdOn
                                binding.tvUpdatedOn.text=it.data.albumDetail.updatedOn
                                binding.tvYear.text=it.data.albumDetail.yearName
                                binding.tvDes.text=it.data.albumDetail.description

                                binding.rvPhotoAlbum.isVisible = true
                                binding.tvNoData.isVisible = false
                                isLoading = true

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

        kidCornerViewModel.getKidsAlbumDetails(
            pageIndex,
            albumID
        )

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

    override fun onItemClick(t: AlbumDetailX, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.kidCornerImageViewFragment,
            bundleOf(
                KidCornerImageViewFragment.PHOTO to t.fullImage,
                KidCornerImageViewFragment.CREATED_BY to t.createdBy,
                KidCornerImageViewFragment.NAME to t.name,
                KidCornerImageViewFragment.DESCRIPTION to t.description,
                KidCornerImageViewFragment.GUIDE_NAME to t.guideBy,
                KidCornerImageViewFragment.CLASSES to t.`class`,
            )
        )
    }


    companion object {
        const val KidId = "id"
        const val AlbumTitle = "albumTitle"
    }

}