package com.app.ecarepro.ui.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentWebViewBinding
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.app.ecarepro.ui.mainActivity

@AndroidEntryPoint
class WebViewFragment : Fragment() {

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private val title by lazy { WebViewFragmentArgs.fromBundle(requireArguments()).title }
    private val url by lazy { WebViewFragmentArgs.fromBundle(requireArguments()).url }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        loadUrl(url)
    }

    private fun loadUrl(url: String) {
        binding.webView.settings.apply {
            builtInZoomControls = false
            displayZoomControls = false
            javaScriptEnabled = true
            loadWithOverviewMode=true
            useWideViewPort=true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        /*wv_url.getSettings().setDisplayZoomControls(true);
               wv_url.getSettings().setBuiltInZoomControls(true);*/
        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            if (isStoragePermission()) {
                val request = DownloadManager.Request(Uri.parse(url))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "download"
                )
                val dm =   requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                mainActivity().showMessage("Downloading...")
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressCircular.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressCircular.hide()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }
        binding.webView.loadUrl(url)
    }
    private fun isStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            val permission = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            storagePermission(permission)
        }
    }
    private fun storagePermission(permission: Array<String>): Boolean {
        for (s in permission) {
            if (PackageManager.PERMISSION_GRANTED != requireActivity().checkSelfPermission(s)) {
                requestPermissions(permission, 100)
                return false
            }
        }
        return true
    }
}