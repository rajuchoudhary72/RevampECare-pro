package com.app.ecarepro.ui.book_library

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentBookLibraryBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.LatestBookViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookLibraryFragment : Fragment() {

    private lateinit var bookLibraryBinding: FragmentBookLibraryBinding
    private val latestBookViewModel: LatestBookViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bookLibraryBinding = FragmentBookLibraryBinding.inflate(inflater, container, false)
        return bookLibraryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            latestBookViewModel._latestBookStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            setUpViewPager(it.data)

                            setUpMegaBook(it.data.megaBookLink)

                        }

                    }


                }
            }
        }

        latestBookViewModel.getLibraryDetails()




        bookLibraryBinding.ivSearch.setOnClickListener {
            findNavController().navigate(R.id.librarySearchFragment)
        }

        bookLibraryBinding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (bookLibraryBinding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_scool -> {

                    bookLibraryBinding.wvMegabook.isVisible = false
                    bookLibraryBinding.viewPager.isVisible = true
                    bookLibraryBinding.tabLayout.isVisible = true


                }

                R.id.btn_class_megabook -> {

                    bookLibraryBinding.wvMegabook.isVisible = true
                    bookLibraryBinding.viewPager.isVisible = false
                    bookLibraryBinding.tabLayout.isVisible = false


                }

                else -> {

                }
            }
        }

    }

    private fun setUpMegaBook(megaBookLink: String) {

        bookLibraryBinding.wvMegabook.zoomIn()
        bookLibraryBinding.wvMegabook.settings.loadWithOverviewMode = true
        bookLibraryBinding.wvMegabook.settings.javaScriptEnabled = true
        bookLibraryBinding.wvMegabook.settings.supportZoom()
        bookLibraryBinding.wvMegabook.settings.builtInZoomControls = true
        bookLibraryBinding.wvMegabook.webViewClient = object : WebViewClient() {


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                (requireActivity() as MainActivity).showLoader(true)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                (requireActivity() as MainActivity).showLoader(false)
                super.onPageFinished(view, url)
            }
        }
        bookLibraryBinding.wvMegabook.loadUrl(megaBookLink)
    }


    private fun setUpViewPager(data: NetworkLatestBook) {

        val tabItem = mutableListOf(getString(R.string.latest_book), getString(R.string.my_account))

        bookLibraryBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {

                return when (position) {
                    0 -> {
                        LatestBookFragment(data.latestBook, 0)
                    }

                    1 -> {
                        LatestBookFragment(data.latestBook, 1)
                    }

                    else -> LatestBookFragment(data.latestBook, 0)
                }
            }

        }

        TabLayoutMediator(
            bookLibraryBinding.tabLayout, bookLibraryBinding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()

    }

}