package com.app.ecarepro.ui.common

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentOpenPdfBinding
import com.app.ecarepro.ui.MainActivity


class OpenPdfFragment : Fragment() {


    private var url: String=""
    private lateinit var openPdfBinding: FragmentOpenPdfBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        openPdfBinding= FragmentOpenPdfBinding.inflate(inflater,container,false)


        url= requireArguments().getString("url").toString()



        return  openPdfBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).showLoader(true)


        openPdfBinding.wvPdf.zoomIn()

        openPdfBinding.wvPdf.settings .loadWithOverviewMode = true
        openPdfBinding.wvPdf.settings.javaScriptEnabled = true
        openPdfBinding.wvPdf.settings.supportZoom()
        openPdfBinding.wvPdf.settings.builtInZoomControls=true

        openPdfBinding.wvPdf.webViewClient= object  : WebViewClient(){



            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                (requireActivity() as MainActivity).showLoader(true)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                (requireActivity() as MainActivity).showLoader(false)
                super.onPageFinished(view, url)
            }
        }

        if (url.isNotEmpty()){
            openPdfBinding.wvPdf.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
        }




    }
}