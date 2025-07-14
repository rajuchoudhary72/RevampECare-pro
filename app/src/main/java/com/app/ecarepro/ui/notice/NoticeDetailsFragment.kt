package com.app.ecarepro.ui.notice

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentNoticeDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NoticeDetailsFragment : Fragment() {

    private var manager: DownloadManager? = null
    private lateinit var fileSource: String
    private lateinit var noticeDetailsBinding: FragmentNoticeDetailsBinding

    private val _noticeDetailsViewModel : NoticeDetailsViewModel by viewModels()

    var noticeID: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        noticeDetailsBinding = FragmentNoticeDetailsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner= viewLifecycleOwner
            noticeDetailsViewModel=_noticeDetailsViewModel
        }
        noticeDetailsBinding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        noticeDetailsBinding.includeToolbar.toolbarTitle.text = getString(R.string.notice_details)
        noticeID= requireArguments().getString(Constant.NOTICE_ID_ARGUMENT)



        return noticeDetailsBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeDetailsBinding.relView.setOnClickListener {
            findNavController().navigate(R.id.action_noticeDetailsFragment_to_openPdfFragment,Bundle( ).apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        }

        noticeDetailsBinding.relDownload.setOnClickListener {
            try {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, getString(R.string.notice) )
            }catch (e:SecurityException){
                e.printStackTrace()
            }
        }

        lifecycleScope.launch {
            _noticeDetailsViewModel._noticeStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            noticeDetailsBinding.noticeDetailData=it.data.notice
                            if (it.data.notice.filePath!=null){
                                fileSource= it.data.notice.filePath.toString()
                            }
                            try {

//                            val htmlWithLineWithNBreaks = it.data.circuler.message.replace("\n", "<br>")
//                            val htmlWithLineWithNRBreaks = htmlWithLineWithNBreaks.replace("\r", "<br>")
//                            val spanned = HtmlCompat.fromHtml(htmlWithLineWithNRBreaks, HtmlCompat.FROM_HTML_MODE_LEGACY)
//                            binding.tvNoticeDetails.text = spanned
//
//                            binding.tvNoticeDetails. movementMethod = LinkMovementMethod.getInstance()

                                val formattedHtml = """
    <html>
    <head>
        <style>
            a { color: blue; text-decoration: underline; }
        </style>
    </head>
    <body>
        ${formatTextWithLinks(it.data.notice.detail)}
    </body>
    </html>
""".trimIndent()


                                noticeDetailsBinding.wvDetails.webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val url = request?.url.toString()
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        startActivity(intent) // Opens in an external browser
                                        return true
                                    }
                                }

                                noticeDetailsBinding.wvDetails.settings.javaScriptEnabled = true
                                noticeDetailsBinding.wvDetails.settings.domStorageEnabled = true
                                noticeDetailsBinding.wvDetails.webViewClient = WebViewClient()
                                noticeDetailsBinding.wvDetails.loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null)
                                noticeDetailsBinding.wvDetails.webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val url = request?.url.toString()
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        startActivity(intent) // Opens the link in the default browser
                                        return true // Return true to prevent WebView from loading the URL
                                    }
                                }

                            }catch (e:NullPointerException){
                                e.message
                            }


                        }
                        }


                }
            }
        }

        _noticeDetailsViewModel.getNoticeDTL(noticeID.orEmpty())

    }


    fun formatTextWithLinks(input: String): String {
        val urlPattern = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]+)"
        return input.replace(Regex(urlPattern)) {
            "<a href='${it.value}'>${it.value}</a>"
        }
    }
}