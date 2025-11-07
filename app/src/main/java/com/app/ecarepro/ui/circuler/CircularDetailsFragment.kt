package com.app.ecarepro.ui.circuler

 import android.content.ClipData
 import android.content.ClipboardManager
 import android.content.Context
 import android.content.Intent
 import android.net.Uri
 import android.os.Build
 import android.os.Bundle
 import android.text.Html
 import android.text.Html.fromHtml
 import android.text.method.LinkMovementMethod
 import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.webkit.WebResourceRequest
 import android.webkit.WebView
 import android.webkit.WebViewClient
 import androidx.core.content.getSystemService
 import androidx.core.text.HtmlCompat
 import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCirculerDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CircularDetailsFragment : Fragment() {

    private lateinit var binding : FragmentCirculerDetailsBinding
     private lateinit var fileSource: String

    private val circularDetailsViewModel:CircularDetailsViewModel by    viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding= FragmentCirculerDetailsBinding.inflate(inflater,container,false)

        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.circuler_details)

        val circularID=  requireArguments().getString(Constant.CIRCULAR_ID)
        if (circularID != null) {
            circularDetailsViewModel.getCircularDTL(circularID)
        }

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.tvCopyHolder.setOnClickListener {
//            copyToClipboard(requireContext(), binding.tvNoticeDetails.text.toString(),getString(R.string.circular)  )
//        }

        binding.relView.setOnClickListener {
            findNavController().navigate(R.id.action_circularDetailsFragment_to_openPdfFragment,Bundle( ).apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        }

        binding.relDownload.setOnClickListener {
            try {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(fileSource, getString(R.string.circular))
            }catch (e:SecurityException){
                e.printStackTrace()
            }
        }

        lifecycleScope.launch {
            circularDetailsViewModel._circularDTLStateFlow.collectLatest {
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
                            try {
                                binding.circularDetails=it.data.circuler
                                fileSource=it.data.circuler.filePath

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
        ${formatTextWithLinks(it.data.circuler.message)}
    </body>
    </html>
""".trimIndent()

                                binding.tvNoticeDetails.apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true

                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            val url = request?.url.toString()
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            startActivity(intent) // opens in external browser
                                            return true
                                        }
                                    }
                                    // ✅ This allows OS-level context menu (like Chrome)
                                    setOnCreateContextMenuListener { menu, v, menuInfo ->
                                        val result = (v as WebView).hitTestResult
                                        val url = result.extra
                                        if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                                            result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                                        ) {

                                            menu.setHeaderTitle(url)
                                            menu.add("Open in Browser").setOnMenuItemClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(url)
                                                    )
                                                )
                                                true
                                            }
                                            menu.add("Copy Link").setOnMenuItemClickListener {
                                                copyTextToClipboard(url ?: "")
                                                true
                                            }
                                            menu.add("Share Link").setOnMenuItemClickListener {
                                                shareLink(url ?: "")
                                                true
                                            }
                                        }
                                    }

                                    binding.tvNoticeDetails.loadDataWithBaseURL(
                                        null,
                                        formattedHtml,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            }catch (e:NullPointerException){
                                e.message
                            }

                        }
                    }
                }
            }
        }
    }

    // Your existing copy function
    private fun copyTextToClipboard(text: String) {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
        // Toast.makeText(requireContext(), "Link Copied", Toast.LENGTH_SHORT).show()
    }
    private fun shareLink(url: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }
    fun formatTextWithLinks(input: String): String {
        val urlPattern = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)"
        return input.replace(Regex(urlPattern)) {
            "<a href='${it.value}'>${it.value}</a>"
        }
    }

    private fun copyToClipboard(context: Context, text: String, label: String ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

}
