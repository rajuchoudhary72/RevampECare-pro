package com.app.ecarepro.ui.common

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentOpenPdfBinding
import com.app.ecarepro.ui.MainActivity
import java.net.URLEncoder


class OpenPdfFragment : Fragment() {


    private var url: String=""
    private lateinit var openPdfBinding: FragmentOpenPdfBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        openPdfBinding= FragmentOpenPdfBinding.inflate(inflater,container,false)
        openPdfBinding.toolbarPdf.setNavigationOnClickListener { findNavController().popBackStack() }


        url= requireArguments().getString("url").toString()



        return  openPdfBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openPdfBinding.wvPdf.zoomIn()

        openPdfBinding.wvPdf.settings .loadWithOverviewMode = true
        openPdfBinding.wvPdf.settings.javaScriptEnabled = true
        openPdfBinding.wvPdf.settings.supportZoom()

        openPdfBinding.wvPdf.settings.builtInZoomControls=true

        try {
            openPdfBinding.wvPdf.webViewClient= object  : WebViewClient(){

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    openPdfBinding.progressCircular.show()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    openPdfBinding.progressCircular.hide()

                }
            }
        }catch (e:IllegalStateException ){
            e.printStackTrace()
        }

        if (url.isNotEmpty()){
            if (url.contains("https://books.google.co")){
                openPdfBinding.wvPdf.loadUrl(url)
            }else if (url.contains("xlsx")){
                val encodedUrl = URLEncoder.encode(url, "UTF-8")
                openPdfBinding.wvPdf.loadUrl("https://view.officeapps.live.com/op/view.aspx?src=$encodedUrl")
              //  openPdfBinding.wvPdf.loadUrl(url)
            }
            else{
                openPdfBinding.wvPdf.loadUrl("https://docs.google.com/viewer?url=$url&embedded=true")
            }
        }
    }
}