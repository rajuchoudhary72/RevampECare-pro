package com.app.ecarepro.feature.docviewer.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DOCViewer(
    modifier: Modifier,
    url: String,
    docType: DocType,
) {
    var isLoading by remember { mutableStateOf(true) }

    val viewerContent = getViewerContent(docType, url)


    Box(modifier = modifier.fillMaxSize()) {
        // AndroidView is a composable that hosts a traditional Android View.
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    // Apply WebView settings once during creation.
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Configure WebViewClient to handle page loading events.
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true // Show loader when page starts loading.
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false // Hide loader when page is finished.
                        }
                    }

                    // Enable JavaScript, zoom controls, and overview mode.
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                }
            },
            update = { webView ->
                // loadDataWithBaseURL is used for HTML content, loadUrl for direct links.
                if (viewerContent.isHtml) {
                    webView.loadDataWithBaseURL(
                        null,
                        viewerContent.content,
                        "text/html",
                        "UTF-8",
                        null
                    )
                } else {
                    webView.loadUrl(viewerContent.content)
                }
            }
        )

        // Show a CircularProgressIndicator in the center while the content is loading.
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


/**
 * A data class to hold the content to be loaded and its type.
 */
private data class ViewerContent(val content: String, val isHtml: Boolean = false)

/**
 * Determines the correct URL or HTML content based on the DocType.
 */
@Composable
private fun getViewerContent(docType: DocType?, url: String): ViewerContent {
    return remember(docType, url) {
        when (docType) {
            DocType.PDF, DocType.DOC -> ViewerContent(
                "https://docs.google.com/viewer?url=${
                    URLEncoder.encode(
                        url,
                        "UTF-8"
                    )
                }&embedded=true"
            )

            DocType.SPREADSHEET -> ViewerContent(
                "https://view.officeapps.live.com/op/view.aspx?src=${
                    URLEncoder.encode(
                        url,
                        "UTF-8"
                    )
                }"
            )
            else -> ViewerContent(url)
        }
    }
}

@Preview(showBackground = true, name = "PDF Document Preview")
@Composable
private fun DocViewerPdfPreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            docType = DocType.PDF
        )
    }
}

@Preview(showBackground = true, name = "Video Player Preview")
@Composable
private fun DocViewerVideoPreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            docType = DocType.VIDEO
        )
    }
}

@Preview(showBackground = true, name = "Image Preview")
@Composable
private fun DocViewerImagePreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "https://www.gstatic.com/webp/gallery/1.jpg",
            docType = DocType.IMAGE
        )
    }
}