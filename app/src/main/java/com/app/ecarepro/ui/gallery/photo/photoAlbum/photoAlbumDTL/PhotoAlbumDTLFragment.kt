package com.app.ecarepro.ui.gallery.photo.photoAlbum.photoAlbumDTL

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Html.fromHtml
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentPhotoAlbumDTLBinding
import com.app.ecarepro.model.Photo
import com.app.ecarepro.model.photo_setting.AlbumSetting
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.xml.sax.XMLReader


@AndroidEntryPoint
class PhotoAlbumDTLFragment : Fragment(), ItemListener<List<Photo>> {

    private var isTextExpanded: Boolean = false
    private lateinit var albumSetting: AlbumSetting
    private lateinit var photoDetails: NetworkAlbumPhotoDetails
    private var photoAlbumId: String = ""
    private lateinit var photoAlbumAdapter: PhotoAlbumDTLAdapter
    private lateinit var binding: FragmentPhotoAlbumDTLBinding
    private val photoAlbumDTLViewModel: PhotoAlbumDTLViewModel by viewModels()

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private var isDataLoaded: Boolean = false
    private var photoPosition: Int = -1

    private var scrollYPosition: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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

        binding.nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            scrollYPosition = scrollY


            // Check if the NestedScrollView has reached the bottom
            if (binding.nestedScrollView.getChildAt(0).bottom <= (v.height + scrollY)) {
                if (isLoading && isDataLoaded) { // Add isDataLoaded check to prevent initial multiple calls
                    isLoading = false
                    pageIndex += 1
                    photoAlbumDTLViewModel.lastPageIndex = pageIndex
                    photoAlbumDTLViewModel.getPhotoAlbumDTL(photoAlbumId, pageIndex)
                }
            }
        }

        binding.tvMore.setOnClickListener {
            binding.tvDes.setLines(binding.tvDes.lineCount)
            binding.tvMore.isVisible = false
            isTextExpanded = true
        }


        if (!isDataLoaded) {
            observeData()
            pageIndex = 1
            getPhotoAlbumDTL()
        } else {
            pageIndex = photoAlbumDTLViewModel.lastPageIndex!!
            photoAlbumAdapter.setData(photoAlbumDTLViewModel.cachedPhotoList)
            setupRecycleViewPager()
            photoAlbumDTLViewModel.cachedData.let {
                if (it != null) {
                    binding.tvHeading.text = it.title

                    binding.tvDes.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fromHtml(it.description, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        fromHtml(it.description)
                    }

                    binding.tvDatePhoto.text = it.eventDate + " | " + it.totalPhotos + " Photos"

                    if (isTextExpanded) {
                        binding.tvMore.isVisible = false
                    } else {
                        binding.tvDes.maxLines = 4
                        binding.tvMore.isVisible = true
                    }
                }
            }
        }


    }

    private fun observeData() {
        lifecycleScope.launch {
            photoAlbumDTLViewModel.photoAlbumStateFlow.observe(viewLifecycleOwner) {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        // binding.rvAlbum.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        //    binding.rvAlbum.isVisible = false
                        isDataLoaded = true
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        isDataLoaded = true
                        binding.rvAlbum.isVisible = true

                        if (it.data != null) {

                            if (it.data.photos != null) {

                                photoAlbumDTLViewModel.cachedData = it.data
                                photoAlbumDTLViewModel.cachedPhotoList.addAll(it.data.photos!!)

                                binding.rvAlbum.isVisible = true
                                binding.tvNoAlbum.isVisible = false
                                isLoading = true

                                binding.tvHeading.text = it.data.title
                                binding.tvDes.maxLines = 4

                                binding.tvDes.text =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        fromHtml(it.data.description, null, ParagraphTagHandler())
                                    } else {
                                        fromHtml(it.data.description,null, ParagraphTagHandler())
                                    }

                                binding.tvDatePhoto.text =
                                    it.data.eventDate + " | " + it.data.totalPhotos + " "+getString(R.string.photos)


                                if (binding.tvDes.lineCount >= 4) {
                                    binding.tvMore.visibility = View.VISIBLE
                                } else {
                                    binding.tvMore.visibility = View.GONE
                                }


                                if (pageIndex == 1) {
                                    photoAlbumAdapter.clearData()
                                }
                                albumSetting = it.data.setting!!
                                photoDetails = it.data
                                photoAlbumAdapter.setData(it.data.photos!!.toMutableList())
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

    }

    private fun getPhotoAlbumDTL() {

        photoAlbumDTLViewModel.getPhotoAlbumDTL(photoAlbumId, pageIndex)
        setupRecycleViewPager()

    }


    private fun setupRecycleViewPager() {/*  binding.rvAlbum.addOnScrollListener(object :
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
                                photoAlbumDTLViewModel.lastPageIndex = pageIndex
                                photoAlbumDTLViewModel.getPhotoAlbumDTL(photoAlbumId, pageIndex)
                            }
                        }

                    }
                }
            }
        })*/
    }

    override fun onItemClick(t: List<Photo>, pos: Int, boolean: Boolean) {
        photoPosition = pos
        photoDetails.photos = t
        findNavController().navigate(
            R.id.photoSliderNavHostFragment, Bundle().apply {
                putParcelable("photoDetails", photoDetails)
                putInt("photoPosition", pos)
                putInt(Constant.GALLERY_TYPE, Constant.GALLERY_TYPE_PHOTO)
            })

    }

    override fun onResume() {
        super.onResume()
        binding.nestedScrollView.post {
            binding.nestedScrollView.scrollTo(0, scrollYPosition)
        }
    }


    class ParagraphTagHandler : Html.TagHandler {
        override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
            if (tag.equals("p", ignoreCase = true) && !opening) {
                output?.append("\n\n") // double new line after </p>
            }
        }
    }

}

