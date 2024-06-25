package com.app.ecarepro.ui.book_library.eBook

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.ecarepro.databinding.FragmentMegaEBookCollectionBinding
import com.app.ecarepro.ui.MainActivity


class MegaEBookCollectionFragment(val megaBookLink: String) : Fragment() {

    private lateinit var binding : FragmentMegaEBookCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMegaEBookCollectionBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMegaBook(megaBookLink)
    }

    private fun setUpMegaBook(megaBookLink: String) {

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