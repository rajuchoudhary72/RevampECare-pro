package com.app.ecarepro.ui.gallery.kid_corner

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.gallery.kid_corner.kid_album_details.KidAlbumDetailsFragment
import com.app.ecarepro.ui.gallery.kid_corner.model.Album
import com.app.ecarepro.ui.gallery.mediaGallery.adapter.SearchByPopUpAdapter

import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

@AndroidEntryPoint
class KidCornerFragment : Fragment(), ItemListener<Album> {


    private var yearId: Int=0
    private var searchByPostition: Int = 0
    private var yearPosition: Int = 0
    private var isSearchBySelected: Boolean = false
    private var isYearSelected: Boolean = false
    private lateinit var kidCornerAdapter: KidCornerAdapter
    private lateinit var binding: FragmentKidCornerBinding
    private val kidCornerViewModel: KidCornerViewModel by viewModels()

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true

    private var searchJob: Job? = null  // Job to handle debounce logic
    private val debounceTime = 300L  // 300ms delay



    private var yearList = mutableListOf<String>()
    private var yearListData = mutableListOf<AcademicYear>()

    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: java.lang.Runnable? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKidCornerBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.title = getString(R.string.kid_corner_title)
        binding.toolbar.isVisible = true
        kidCornerAdapter = KidCornerAdapter(this@KidCornerFragment)

        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 2)
            adapter = kidCornerAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edSearch.doAfterTextChanged { text ->
            val query = text?.toString()?.trim() ?: ""
            if (query != kidCornerViewModel.lastSearchQuery) {
                kidCornerViewModel.lastSearchQuery = query

                // Cancel the previous search request
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                // Schedule a new search request with a delay
                searchRunnable = Runnable {
                    pageIndex=1
                    kidCornerViewModel.pageIndex=1
                    kidCornerViewModel.getSearchKidsAlbum(
                        pageIndex,
                        yrID = kidCornerViewModel.academicYearID,
                        keyword = text.toString()
                    )
                }
                searchHandler.postDelayed(searchRunnable!!, 500) // 500ms delay
            }
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

                            if (it.data.academicYears != null) {
                            if (it.data.academicYears.isNotEmpty()) {
                                yearList.clear()
                                yearListData.clear()
                                it.data.academicYears.forEach { year ->
                                    yearList.add(year.session)
                                }
                                yearListData = it.data.academicYears.toMutableList()
                                binding.tvYear.text=yearListData[0].session
                                kidCornerViewModel.academicYearID=yearListData[0].yrID
                                kidCornerViewModel.academicYear=yearListData[0].session
                                yearId=yearListData[0].yrID

                            }
                            }

                            if (it.data.albums != null) {

                                if (pageIndex == 1) {
                                    kidCornerAdapter.clearData()
                                    binding.rvPhotoAlbum.isVisible = true
                                    binding.tvNoData.isVisible = false
                                }
                                isLoading = true

                                kidCornerViewModel.cacheListData.addAll(it.data.albums)
                                kidCornerAdapter.setData(it.data.albums.toMutableList())

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


        binding.tvYear.setOnClickListener {
            popUpYear()
        }

        if (kidCornerViewModel.isFirst){
            kidCornerViewModel.getKidsCornerAlbums(
                pageIndex,
            )
            kidCornerViewModel.isFirst=false
        }else{
           pageIndex= kidCornerViewModel.pageIndex
           // kidCornerAdapter.setData(kidCornerViewModel.cacheListData)
           binding. tvYear.text= kidCornerViewModel.academicYear
            yearId= kidCornerViewModel.academicYearID
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
                                kidCornerViewModel.getKidsCornerAlbums(
                                    pageIndex,
                                )
                            }
                        }

                    }
                }
            }
        })


    }

    override fun onItemClick(malbum: Album, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.kidAlbumDetailsFragment,
            bundleOf(
                KidAlbumDetailsFragment.KidId to malbum.kid,
                KidAlbumDetailsFragment.AlbumTitle to malbum.title,
            )
        )
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
                yearId=yearListData[yearPosition].yrID
                kidCornerViewModel.academicYearID=yearListData[yearPosition].yrID

                pageIndex=1
                kidCornerViewModel.academicYear=yearList[yearPosition]
                kidCornerViewModel.cacheListData.clear()
                kidCornerViewModel.getSearchKidsAlbum(
                    pageIndex,
                    yrID = yearId,
                    keyword = null
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

}