package com.app.ecarepro.ui.gallery.mediaGallery

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
import com.app.ecarepro.data.network.model.Album
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentMediaGalleryBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.gallery.mediaGallery.adapter.SearchByPopUpAdapter
import com.app.ecarepro.ui.gallery.mediaGallery.mediaDetails.MediaDetailsFragment

import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

@AndroidEntryPoint
class MediaGalleryFragment : Fragment(), ItemListener<Album> {


    private var searchByPostition: Int = 0
    private var yearPosition: Int = 0
    private var isSearchBySelected: Boolean = false
    private var isYearSelected: Boolean = false
    private lateinit var mediaGalleryAdapter: MediaGalleryAdapter
    private lateinit var binding: FragmentMediaGalleryBinding
    private val mediaGalleryViewModel: MediaGalleryViewModel by viewModels()

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true

    private var searchJob: Job? = null  // Job to handle debounce logic
    private val debounceTime = 300L  // 300ms delay

    private val searchByList =
        mutableListOf<String>(getString(R.string.mediaGallery_search_all_search),
            getString(R.string.mediaGallery_search_newspaper), getString(R.string.mediaGallery_search_headline),
            getString(R.string.mediaGallery_search_publish_date), getString(R.string.mediaGallery_search_year))

    private var yearList = mutableListOf<String>()

    private var queryType = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaGalleryBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.title = getString(R.string.media_gallery_title)
        binding.toolbar.isVisible = true
        mediaGalleryAdapter = MediaGalleryAdapter(this@MediaGalleryFragment)

        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = mediaGalleryAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.edSearch.doAfterTextChanged { text ->
                searchJob?.cancel() // Cancel previous job if user types again

                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(debounceTime)  // Wait for user to stop typing

                    mediaGalleryViewModel.getMediaGallery(
                        pageIndex,
                        queryType,
                        0,
                        binding.tvPubDate.text.toString(),
                        text.toString()
                    )
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }


        binding.tvPubDate.text = Constant.currentDate()
        try {
            binding.tvPubDate.setOnClickListener {
                ECareDataPicker(
                    requireActivity(),
                    false,
                    object : ECareDataPicker.PickerCallback {
                        override fun onSelect(date: String?, isCurrentDate: Boolean) {
                            binding.tvPubDate.text = date
                            mediaGalleryViewModel.getMediaGallery(
                                pageIndex,
                                queryType,
                                yearList[yearPosition].toInt(),
                                binding.tvPubDate.text.toString(),
                                binding.edSearch.text.toString()
                            )
                        }
                    }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }


        lifecycleScope.launch {
            mediaGalleryViewModel.mediaGalleryStateFlow.collectLatest {
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

                            if (it.data.years != null) {
                                yearList = it.data.years as MutableList<String>

                            }

                            if (it.data.albums != null) {

                                if (pageIndex == 1) {
                                    mediaGalleryAdapter.clearData()
                                }

                                binding.rvPhotoAlbum.isVisible = true
                                binding.tvNoData.isVisible = false
                                isLoading = true

                                mediaGalleryAdapter.setData(it.data.albums.toMutableList())

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


        binding.tvSearchBy.setOnClickListener {
            popUpSearchBy()
        }

        binding.tvYear.setOnClickListener {
            popUpYear()
        }

        mediaGalleryViewModel.getMediaGallery(
            pageIndex,
            queryType,
            0,
            binding.tvPubDate.text.toString(),
            binding.edSearch.text.toString()
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
                                mediaGalleryViewModel.getMediaGallery(
                                    pageIndex,
                                    queryType,
                                    yearList[yearPosition].toInt(),
                                    binding.tvPubDate.text.toString(),
                                    binding.edSearch.text.toString()
                                )
                            }
                        }

                    }
                }
            }
        })


    }

    override fun onItemClick(t: Album, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.mediaDetailsFragment,
            bundleOf(
                MediaDetailsFragment.description to t.description,
                MediaDetailsFragment.fileName to t.fileName,
                MediaDetailsFragment.fileNameFullSize to t.fileNameFullSize,
                MediaDetailsFragment.headline to t.headline,
                MediaDetailsFragment.newsName to t.newsName,
                MediaDetailsFragment.publishedOn to t.publishedOn,
                MediaDetailsFragment.updatedOn to t.updatedOn,
            )
        )
    }

    private fun popUpSearchBy() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)

        tvHeading.text = getString(R.string.select_search_by)
        builder.setView(view)


        relOk.setOnClickListener {

            if (isSearchBySelected) {
                pageIndex = 1
                binding.tvSearchBy.text = searchByList[searchByPostition]
                queryType = searchByPostition
                setupSearchByDropDown(searchByPostition)

                builder.dismiss()
            }


        }

        val staffPopUpListAdapter =
            SearchByPopUpAdapter(searchByList, object : ItemListener<String> {
                override fun onItemClick(t: String, pos: Int, boolean: Boolean) {
                    isSearchBySelected = true
                    searchByPostition = pos
                }
            })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = staffPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun popUpYear() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)

        tvHeading.text = getString(R.string.select_year)
        builder.setView(view)


        relOk.setOnClickListener {

            if (isYearSelected) {
                binding.tvYear.text = yearList[yearPosition]
                pageIndex = 1
                mediaGalleryViewModel.getMediaGallery(
                    pageIndex,
                    queryType,
                    yearList[yearPosition].toInt(),
                    binding.tvPubDate.text.toString(),
                    binding.edSearch.text.toString()
                )
                builder.dismiss()
            }


        }

        val staffPopUpListAdapter =
            SearchByPopUpAdapter(yearList, object : ItemListener<String> {
                override fun onItemClick(t: String, pos: Int, boolean: Boolean) {
                    isYearSelected = true
                    yearPosition = pos
                }
            })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = staffPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun setupSearchByDropDown(searchByPostition: Int) {
        try {
            when (searchByPostition) {
                0 -> {
                    binding.tvYear.isVisible = false
                    binding.edSearch.isVisible = false
                    binding.tvPubDate.isVisible = false

                    mediaGalleryViewModel.getMediaGallery(
                        pageIndex,
                        queryType,
                        yearList[yearPosition].toInt(),
                        binding.tvPubDate.text.toString(),
                        binding.edSearch.text.toString()
                    )


                }

                1 -> {
                    binding.tvYear.isVisible = false
                    binding.edSearch.isVisible = true
                    binding.edSearch.hint = getString(R.string.enter_newspaper)
                    binding.edSearch.setText("")

                    binding.tvPubDate.isVisible = false
                }

                2 -> {
                    binding.tvYear.isVisible = false
                    binding.edSearch.isVisible = true
                    binding.edSearch.hint = getString(R.string.enter_headline)
                    binding.tvPubDate.isVisible = false
                    binding.edSearch.setText("")
                }

                3 -> {
                    binding.tvYear.isVisible = false
                    binding.edSearch.isVisible = false
                    binding.tvPubDate.isVisible = true
                    binding.edSearch.setText("")
                }

                4 -> {
                    binding.tvYear.isVisible = true
                    binding.edSearch.isVisible = false
                    binding.tvPubDate.isVisible = false
                    binding.edSearch.setText("")
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}