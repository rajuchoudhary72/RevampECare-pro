package com.app.ecarepro.ui.book_library.eBook

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentMegaEBookCollectionBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MegaEBookCollectionFragment() : Fragment() {

    private lateinit var binding : FragmentMegaEBookCollectionBinding
    private val eBookViewModel: EBookViewModel by viewModels()
    private var megaBookLink: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMegaEBookCollectionBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        megaBookLink = arguments?.getString("link")

        megaBookLink?.let { setUpMegaBook(it) }


        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (binding.wvMegabook.canGoBack()) {
                    binding.wvMegabook.goBack()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    fun onBackPress(){
        if (binding.wvMegabook.canGoBack()) {
            binding.wvMegabook.goBack()
        }else{
            findNavController().popBackStack()
        }
    }



    private fun setUpMegaBook(megaBookLink: String) {
        (requireActivity() as MainActivity).showLoader(true)
        binding.wvMegabook.zoomIn()
        binding.wvMegabook.settings .loadWithOverviewMode = true
        binding.wvMegabook.settings.javaScriptEnabled = true
        binding.wvMegabook.settings.supportZoom()
        binding.wvMegabook.settings.builtInZoomControls=true
        binding.wvMegabook.webViewClient= object  : WebViewClient(){



            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                (requireActivity() as MainActivity).showLoader(true)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                (requireActivity() as MainActivity).showLoader(false)
                super.onPageFinished(view, url)
            }
        }
        binding.wvMegabook.loadUrl(megaBookLink)
    }

}