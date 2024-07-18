package com.app.ecarepro.ui.gallery.photo.photoAlbum.photoAlbumDTL

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
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
import com.app.ecarepro.databinding.FragmentPhotoAlbumDTLBinding
import com.app.ecarepro.model.Photo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.gallery.photo.photo_slider.PhotoSliderFragment
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhotoAlbumDTLFragment : Fragment(), ItemListener<Photo> {

    private var photoAlbumId: String = ""
    private lateinit var photoAlbumAdapter: PhotoAlbumDTLAdapter
    private lateinit var binding: FragmentPhotoAlbumDTLBinding
    private val photoAlbumDTLViewModel: PhotoAlbumDTLViewModel by viewModels()

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoAlbumDTLBinding.inflate(inflater, container, false)
        photoAlbumId = requireArguments().getString(Constant.ID).toString()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        photoAlbumAdapter = PhotoAlbumDTLAdapter(this@PhotoAlbumDTLFragment)
        binding.rvAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 4)
            adapter = photoAlbumAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvMore.setOnClickListener {
            binding.tvDes.setLines(binding.tvDes.lineCount)
        }

        lifecycleScope.launch {
            photoAlbumDTLViewModel.photoAlbumStateFlow.collectLatest {
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

                        if (it.data != null) {

                            if (it.data.photos != null) {

                                binding.rvAlbum.isVisible = true
                                binding.tvNoAlbum.isVisible = false
                                isLoading = true

                                binding.tvHeading.text = it.data.title

                                binding.tvDes.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    fromHtml(it.data.description, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    fromHtml(it.data.description)
                                }

                                binding.tvDes.text = it.data.description
                                binding.tvDatePhoto.text =
                                    it.data.eventDate + " | " + it.data.totalPhotos + " Photos"

                                if (binding.tvDes.getLineCount() >= 4) {
                                    binding.tvMore.setVisibility(View.VISIBLE)
                                } else {
                                    binding.tvMore.setVisibility(View.GONE)
                                }

                                if (pageIndex==1){
                                    photoAlbumAdapter.clearData()
                                }

                                photoAlbumAdapter.setData(it.data.photos.toMutableList())

                            } else {
                                if (pageIndex == 1) {
                                    binding.rvAlbum.isVisible = false
                                    binding.tvNoAlbum.isVisible = true
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }


        }

        photoAlbumDTLViewModel.getPhotoAlbumDTL(photoAlbumId, pageIndex)

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
                                photoAlbumDTLViewModel.getPhotoAlbumDTL(photoAlbumId, pageIndex)
                            }
                        }

                    }
                }
            }
        })
    }

    override fun onItemClick(t: Photo, pos: Int, boolean: Boolean) {

        findNavController().navigate(R.id.action_photoAlbumDTLFragment_to_photoSliderFragment ,
            Bundle().apply {
                putString(Constant.ID, t.id)
                putString(Constant.URL_ARGUMENT, t.photoPath)
                putInt(Constant.GALLERY_TYPE, Constant.GALLERY_TYPE_PHOTO)
                putBoolean("isLiked", t.isLike)
                putBoolean("isFav", t.isFavourite)
                putInt("likes", t.likes)
            })

    }

}