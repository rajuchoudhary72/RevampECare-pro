package com.app.ecarepro.designsystem.core.component

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import java.net.URLEncoder

// ─────────────────────────────────────────────────────────────────────────────
// State
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Holds a reference to the underlying [WebView] so the caller can perform
 * imperative actions such as back-navigation and page reload.
 *
 * Obtain an instance with [rememberEcareProWebViewState].
 */
class EcareProWebViewState {
    internal var webView: WebView? = null

    /**
     * Navigate back in the WebView's history.
     * @return `true` if the WebView consumed the back press; `false` when there
     * is no more history (or when the current view is an image/video/audio viewer)
     * — the caller should then handle navigation itself.
     */
    fun navigateBack(): Boolean {
        val wv = webView ?: return false
        return if (wv.canGoBack()) { wv.goBack(); true } else false
    }

    /** Reload the currently loaded page. No-op when showing an image. */
    fun reload() {
        webView?.reload()
    }
}

/** Creates and remembers an [EcareProWebViewState] across recompositions. */
@Composable
fun rememberEcareProWebViewState(): EcareProWebViewState = remember { EcareProWebViewState() }

// ─────────────────────────────────────────────────────────────────────────────
// Main composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A comprehensive, reusable file-viewer / WebView composable that covers every
 * [DocType] — including download support for all types.
 *
 * ### Full coverage matrix
 *
 * | Type | Preview | Download |
 * |---|---|---|
 * | **IMAGE** (jpg/png/webp/gif/bmp/svg…) | Native zoomable viewer — pinch-to-zoom, double-tap toggle, pan | Download FAB → [DownloadManager] |
 * | **PDF** | Mozilla PDF.js (full toolbar) | PDF.js toolbar button → [DownloadManager] via `DownloadListener` |
 * | **DOC / DOCX** | Google Docs online viewer | Google Docs download → [DownloadManager] via `DownloadListener` |
 * | **XLS / XLSX / CSV** | Microsoft Office Online viewer | Office Online download → [DownloadManager] via `DownloadListener` |
 * | **PPT / PPTX** | Microsoft Office Online viewer | Office Online download → [DownloadManager] via `DownloadListener` |
 * | **VIDEO** (mp4/mov/avi/mkv…) | HTML5 `<video controls>` in WebView | Download FAB → [DownloadManager] |
 * | **AUDIO** (mp3/wav/ogg/m4a…) | HTML5 `<audio controls>` in WebView | Download FAB → [DownloadManager] |
 * | **Web page / Form** | Full WebView — JS, cookies, file-upload `<input>` | `DownloadListener` for `attachment` links |
 *
 * ### Usage
 * ```kotlin
 * val webViewState = rememberEcareProWebViewState()
 *
 * EcareProWebView(
 *     url   = "https://school.example.com/photo.jpg",  // DocType auto-detected
 *     state = webViewState,
 *     enableDownload = true,
 *     onDownloadQueued = { fileName -> /* show snackbar */ },
 * )
 *
 * // Wire system back-press
 * BackHandler { if (!webViewState.navigateBack()) navigateUp() }
 * ```
 *
 * @param url              URL to load — web page, direct file link, or authenticated endpoint.
 * @param modifier         Modifier applied to the root [Box].
 * @param docType          Explicit [DocType] override; `null` = auto-detected from URL extension.
 *                         Pass `null` for plain web / form pages so no proxy viewer is injected.
 * @param state            [EcareProWebViewState] exposing [EcareProWebViewState.navigateBack]
 *                         and [EcareProWebViewState.reload].
 * @param enableDownload   Enables download for every file type:
 *                         IMAGE / VIDEO / AUDIO → Download FAB;
 *                         PDF / DOC / XLSX / PPT → viewer's own download button → `DownloadListener`.
 * @param enableFileUpload Allows `<input type="file">` forms to open the native document picker.
 * @param onPageTitle      Called with the page `<title>` once the page loads.
 * @param onDownloadQueued Called after [DownloadManager] accepts the download; parameter = file name.
 * @param onError          Called when the main document fails to load.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EcareProWebView(
    url: String,
    modifier: Modifier = Modifier,
    docType: DocType? = null,
    state: EcareProWebViewState = rememberEcareProWebViewState(),
    enableDownload: Boolean = true,
    enableFileUpload: Boolean = false,
    onPageTitle: (String) -> Unit = {},
    onDownloadQueued: (fileName: String) -> Unit = {},
    onError: (description: String) -> Unit = {},
) {
    // Resolve the effective type once; auto-detect from URL extension when null
    val effectiveDocType = remember(url, docType) { docType ?: DocType.fromUrl(url) }

    when (effectiveDocType) {
        DocType.IMAGE -> ImageViewer(
            url = url,
            modifier = modifier,
            enableDownload = enableDownload,
            onDownloadQueued = onDownloadQueued,
        )

        else -> WebViewContent(
            url = url,
            modifier = modifier,
            docType = effectiveDocType,
            state = state,
            enableDownload = enableDownload,
            enableFileUpload = enableFileUpload,
            onPageTitle = onPageTitle,
            onDownloadQueued = onDownloadQueued,
            onError = onError,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// IMAGE — native Compose viewer
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Full-screen image viewer with:
 * - Pinch-to-zoom (1×–6×) with clamped pan
 * - Double-tap to toggle between fit (1×) and 2.5× zoom
 * - Loading spinner while Coil fetches the image
 * - Error message when the image fails to load
 * - Download FAB (bottom-right) that saves to the public Downloads folder via [DownloadManager]
 */
@Composable
private fun ImageViewer(
    url: String,
    modifier: Modifier = Modifier,
    enableDownload: Boolean,
    onDownloadQueued: (String) -> Unit,
) {
    val context = LocalContext.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 6f)
        val maxX = (newScale - 1f) * 600f
        val maxY = (newScale - 1f) * 800f
        scale = newScale
        offset = Offset(
            x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX),
            y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY),
        )
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onLoading = { isLoading = true; hasError = false },
            onSuccess = { isLoading = false },
            onError = { isLoading = false; hasError = true },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    clip = true,
                )
                .transformable(transformState)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2.5f
                            }
                        },
                    )
                },
        )

        // Loading spinner
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Error state
        if (hasError) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                Text(
                    text = "Unable to load image",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Download FAB — only visible after the image loads successfully
        if (enableDownload && !isLoading && !hasError) {
            FloatingActionButton(
                onClick = { downloadDirectUrl(context, url, onDownloadQueued) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "Download image",
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// WebView — PDF / DOC / XLSX / PPT / VIDEO / AUDIO / web pages
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContent(
    url: String,
    modifier: Modifier,
    docType: DocType?,
    state: EcareProWebViewState,
    enableDownload: Boolean,
    enableFileUpload: Boolean,
    onPageTitle: (String) -> Unit,
    onDownloadQueued: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current

    var loadingProgress by remember { mutableIntStateOf(0) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Pending ValueCallback for <input type="file"> in forms
    var fileChooserCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        fileChooserCallback?.onReceiveValue(
            if (uris.isEmpty()) null else uris.toTypedArray()
        )
        fileChooserCallback = null
    }

    val resolvedContent = remember(url, docType) { resolveViewerContent(url, docType) }
    val lastLoadedContent = remember { mutableStateOf<String?>(null) }

    // VIDEO and AUDIO: DownloadListener never fires for the page's own media URL,
    // so we show a manual download FAB instead.
    val showDownloadFab = enableDownload &&
            (docType == DocType.VIDEO || docType == DocType.AUDIO)

    Box(modifier = modifier) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                    // ── Settings ──────────────────────────────────────────
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        allowFileAccess = true
                        allowContentAccess = true
                        // Allow video/audio auto-play (needed for our HTML wrappers)
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            safeBrowsingEnabled = false
                        }
                        cacheMode = WebSettings.LOAD_DEFAULT
                        textZoom = 100
                    }

                    // ── Cookies ───────────────────────────────────────────
                    // setAcceptThirdPartyCookies(WebView, Boolean) requires the WebView instance
                    // as its first argument. `this` here is the WebView (outer apply scope).
                    CookieManager.getInstance().let { cm ->
                        cm.setAcceptCookie(true)
                        cm.setAcceptThirdPartyCookies(this, true)
                    }

                    // ── WebViewClient ─────────────────────────────────────
                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(view: WebView, pageUrl: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, pageUrl, favicon)
                            hasError = false
                        }

                        override fun onPageFinished(view: WebView, pageUrl: String?) {
                            super.onPageFinished(view, pageUrl)
                            CookieManager.getInstance().flush()
                            onPageTitle(view.title ?: "")
                        }

                        override fun onReceivedError(
                            view: WebView,
                            request: WebResourceRequest,
                            error: WebResourceError,
                        ) {
                            // Only surface errors for the main document, not sub-resources
                            if (request.isForMainFrame) {
                                val desc = error.description?.toString() ?: "Failed to load"
                                hasError = true
                                errorMessage = desc
                                onError(desc)
                            }
                        }

                        // Open tel:, mailto:, intent:, whatsapp: in the correct system app
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            val reqUrl = request.url.toString()
                            return when {
                                reqUrl.startsWith("tel:") ||
                                reqUrl.startsWith("mailto:") ||
                                reqUrl.startsWith("intent:") ||
                                reqUrl.startsWith("whatsapp:") -> {
                                    try {
                                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl)))
                                    } catch (_: Exception) {}
                                    true
                                }
                                else -> false
                            }
                        }
                    }

                    // ── WebChromeClient ───────────────────────────────────
                    webChromeClient = object : WebChromeClient() {

                        override fun onProgressChanged(view: WebView, newProgress: Int) {
                            loadingProgress = newProgress
                        }

                        override fun onReceivedTitle(view: WebView, title: String?) {
                            onPageTitle(title ?: "")
                        }

                        // File chooser for <input type="file"> forms
                        override fun onShowFileChooser(
                            webView: WebView,
                            filePathCallback: ValueCallback<Array<Uri>>,
                            fileChooserParams: FileChooserParams,
                        ): Boolean {
                            if (!enableFileUpload) {
                                filePathCallback.onReceiveValue(null)
                                return false
                            }
                            fileChooserCallback?.onReceiveValue(null)
                            fileChooserCallback = filePathCallback

                            val acceptTypes = fileChooserParams.acceptTypes
                                .filter { it.isNotBlank() }
                                .takeIf { it.isNotEmpty() }
                                ?.toTypedArray()
                                ?: arrayOf("*/*")

                            filePickerLauncher.launch(acceptTypes)
                            return true
                        }

                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String,
                            callback: GeolocationPermissions.Callback,
                        ) {
                            callback.invoke(origin, true, false)
                        }
                    }

                    // ── Download listener ─────────────────────────────────
                    // Catches: PDF.js / Docs / Office download buttons,
                    //          <a download="…"> links, Content-Disposition: attachment.
                    // Does NOT fire for direct media URLs (VIDEO/AUDIO) — those
                    // are handled by the manual FAB above.
                    if (enableDownload) {
                        setDownloadListener { dlUrl, userAgent, contentDisposition, mimeType, _ ->
                            queueDownload(
                                context = ctx,
                                url = dlUrl,
                                userAgent = userAgent,
                                contentDisposition = contentDisposition,
                                mimeType = mimeType,
                                onQueued = onDownloadQueued,
                            )
                        }
                    }
                }
            },
            update = { webView ->
                state.webView = webView
                if (lastLoadedContent.value != resolvedContent.content) {
                    lastLoadedContent.value = resolvedContent.content
                    if (resolvedContent.isHtml) {
                        // Use the source URL as baseUrl so the <video>/<audio> src
                        // can be fetched cross-origin without CORS issues
                        webView.loadDataWithBaseURL(
                            url,
                            resolvedContent.content,
                            "text/html",
                            "UTF-8",
                            null,
                        )
                    } else {
                        webView.loadUrl(resolvedContent.content)
                    }
                }
            },
        )

        // ── Progress bar ──────────────────────────────────────────────────
        if (loadingProgress < 100) {
            LinearProgressIndicator(
                progress = { loadingProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        // ── Download FAB for VIDEO and AUDIO ──────────────────────────────
        if (showDownloadFab) {
            FloatingActionButton(
                onClick = { downloadDirectUrl(context, url, onDownloadQueued) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "Download file",
                )
            }
        }

        // ── Error overlay ─────────────────────────────────────────────────
        if (hasError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Unable to load content",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = { state.reload() }) {
                    Text("Retry")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            state.webView?.apply {
                stopLoading()
                clearHistory()
                destroy()
            }
            state.webView = null
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Viewer URL / HTML resolution
// ─────────────────────────────────────────────────────────────────────────────

private data class ViewerContent(
    /** The URL string OR raw HTML markup to load. */
    val content: String,
    /** `true` → load with `loadDataWithBaseURL`; `false` → load with `loadUrl`. */
    val isHtml: Boolean = false,
)

/**
 * Maps a raw file URL + [DocType] to the correct [ViewerContent].
 *
 * | Input | Result |
 * |---|---|
 * | `DocType.PDF` | Mozilla PDF.js hosted viewer URL |
 * | `DocType.DOC` | Google Docs online viewer URL |
 * | `DocType.SPREADSHEET` | Microsoft Office Online viewer URL |
 * | `DocType.VIDEO` | Self-contained HTML page with `<video controls>` |
 * | `DocType.AUDIO` | Self-contained HTML page with `<audio controls>` |
 * | `.ppt` / `.pptx` extension (no DocType) | Microsoft Office Online viewer URL |
 * | Everything else | Direct URL (loaded as-is) |
 */
private fun resolveViewerContent(url: String, docType: DocType?): ViewerContent {
    val encoded = URLEncoder.encode(url, "UTF-8")

    return when (docType) {

        DocType.PDF -> ViewerContent(
            "https://mozilla.github.io/pdf.js/web/viewer.html?file=$encoded"
        )

        DocType.DOC -> ViewerContent(
            "https://docs.google.com/viewer?url=$encoded&embedded=true"
        )

        DocType.SPREADSHEET -> ViewerContent(
            "https://view.officeapps.live.com/op/view.aspx?src=$encoded"
        )

        DocType.VIDEO -> ViewerContent(
            content = buildVideoHtml(url),
            isHtml = true,
        )

        DocType.AUDIO -> ViewerContent(
            content = buildAudioHtml(url),
            isHtml = true,
        )

        else -> {
            // Route PPT/PPTX through Office Online even though they are not in DocType
            val ext = url.substringAfterLast('.', "").lowercase().take(5)
            if (ext == "ppt" || ext == "pptx") {
                ViewerContent("https://view.officeapps.live.com/op/view.aspx?src=$encoded")
            } else {
                ViewerContent(url)
            }
        }
    }
}

/**
 * Minimal HTML page that plays [videoUrl] using the HTML5 `<video>` element.
 * Using a data-URL base with the source URL as `baseUrl` in [WebView.loadDataWithBaseURL]
 * allows the browser context to fetch the cross-origin video stream without CORS issues.
 */
private fun buildVideoHtml(videoUrl: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body { background:#000; display:flex; align-items:center;
               justify-content:center; height:100vh; overflow:hidden; }
        video { width:100%; height:100%; object-fit:contain; }
      </style>
    </head>
    <body>
      <video controls autoplay playsinline preload="metadata">
        <source src="$videoUrl">
        Your device does not support video playback.
      </video>
    </body>
    </html>
""".trimIndent()

/**
 * Minimal HTML page that plays [audioUrl] using the HTML5 `<audio>` element,
 * with a centred card layout so it looks reasonable on any screen size.
 */
private fun buildAudioHtml(audioUrl: String): String {
    val fileName = audioUrl.substringAfterLast('/').substringBefore('?')
    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body { background:#f5f5f5; display:flex; flex-direction:column;
               align-items:center; justify-content:center;
               height:100vh; padding:24px; font-family:sans-serif; gap:16px; }
        .filename { color:#333; font-size:14px; text-align:center;
                    word-break:break-all; max-width:100%; }
        audio { width:100%; }
      </style>
    </head>
    <body>
      <p class="filename">$fileName</p>
      <audio controls autoplay preload="metadata">
        <source src="$audioUrl">
        Your device does not support audio playback.
      </audio>
    </body>
    </html>
    """.trimIndent()
}

// ─────────────────────────────────────────────────────────────────────────────
// Download helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Enqueues a download triggered **from inside the WebView** (PDF.js button,
 * Google Docs button, Office Online button, `<a download>` link, or any
 * `Content-Disposition: attachment` response).
 *
 * Auth cookies stored by the WebView are forwarded so that authenticated
 * endpoints continue to work from [DownloadManager].
 */
private fun queueDownload(
    context: Context,
    url: String,
    userAgent: String,
    contentDisposition: String,
    mimeType: String,
    onQueued: (String) -> Unit,
) {
    val rawName = URLUtil.guessFileName(url, contentDisposition, mimeType)
    val fileName = ensureCorrectExtension(rawName, mimeType)

    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setTitle(fileName)
        setDescription("Downloading…")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        addRequestHeader("User-Agent", userAgent)
        // Forward session cookies → authenticated endpoints work without re-login
        CookieManager.getInstance().getCookie(url)
            ?.takeIf { it.isNotBlank() }
            ?.let { addRequestHeader("Cookie", it) }
        if (mimeType.isNotBlank()) setMimeType(mimeType)
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
    }

    (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    onQueued(fileName)
}

/**
 * Enqueues a download triggered **outside the WebView** — the Download FAB
 * shown for IMAGE, VIDEO, and AUDIO types.
 *
 * Cookies stored by the WebView for [url]'s domain are forwarded automatically
 * so that authenticated CDN / S3 URLs download correctly.
 */
private fun downloadDirectUrl(
    context: Context,
    url: String,
    onQueued: (String) -> Unit,
) {
    val ext = url.substringAfterLast('.', "").lowercase().take(6)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        ?: "application/octet-stream"

    val rawName = url.substringAfterLast('/').substringBefore('?').trim()
    val fileName = ensureCorrectExtension(
        rawName.takeIf { it.isNotBlank() } ?: "file_${System.currentTimeMillis()}",
        mimeType,
    )

    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setTitle(fileName)
        setDescription("Downloading…")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        CookieManager.getInstance().getCookie(url)
            ?.takeIf { it.isNotBlank() }
            ?.let { addRequestHeader("Cookie", it) }
        if (mimeType.isNotBlank()) setMimeType(mimeType)
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
    }

    (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    onQueued(fileName)
}

/**
 * If [fileName] has a generic / missing extension (`.bin`, no extension) and a
 * concrete MIME type is known, replaces the extension with the canonical one so
 * the downloaded file opens in the right app automatically.
 */
private fun ensureCorrectExtension(fileName: String, mimeType: String): String {
    if (mimeType.isBlank() || mimeType == "application/octet-stream") return fileName
    val mimeExt = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: return fileName
    val currentExt = fileName.substringAfterLast('.', "")
    return if (currentExt.equals(mimeExt, ignoreCase = true)) fileName
    else "${fileName.substringBeforeLast('.')}.$mimeExt"
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Image viewer")
@Composable
private fun PreviewImageViewer() {
    EcareProTheme {
        EcareProWebView(
            url = "https://www.gstatic.com/webp/gallery/1.jpg",
            docType = DocType.IMAGE,
            enableDownload = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "PDF via PDF.js")
@Composable
private fun PreviewWebViewPdf() {
    EcareProTheme {
        EcareProWebView(
            url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            docType = DocType.PDF,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "Word document")
@Composable
private fun PreviewWebViewDoc() {
    EcareProTheme {
        EcareProWebView(
            url = "https://example.com/sample.docx",
            docType = DocType.DOC,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "Excel spreadsheet")
@Composable
private fun PreviewWebViewExcel() {
    EcareProTheme {
        EcareProWebView(
            url = "https://example.com/report.xlsx",
            docType = DocType.SPREADSHEET,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "PowerPoint presentation")
@Composable
private fun PreviewWebViewPpt() {
    EcareProTheme {
        EcareProWebView(
            url = "https://example.com/slides.pptx",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "Video player")
@Composable
private fun PreviewWebViewVideo() {
    EcareProTheme {
        EcareProWebView(
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            docType = DocType.VIDEO,
            enableDownload = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "Audio player")
@Composable
private fun PreviewWebViewAudio() {
    EcareProTheme {
        EcareProWebView(
            url = "https://example.com/audio.mp3",
            docType = DocType.AUDIO,
            enableDownload = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, name = "Plain web page / form")
@Composable
private fun PreviewWebViewPage() {
    EcareProTheme {
        EcareProWebView(
            url = "https://www.google.com",
            enableFileUpload = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
