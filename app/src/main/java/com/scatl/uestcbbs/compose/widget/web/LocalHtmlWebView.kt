package com.scatl.uestcbbs.compose.widget.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.api.entity.AttachmentEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toBBSImgUrl
import com.scatl.uestcbbs.compose.ext.toHexNoAlpha
import com.scatl.uestcbbs.compose.ext.toHexWithAlpha
import com.scatl.uestcbbs.compose.ext.toIntColor
import com.scatl.uestcbbs.compose.manager.ThemeManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.HtmlUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by sca_tl at 2024/8/6 18:47:32
 */
@Composable
fun LocalHtmlWebView(
    content: String?,
    format: Int?,
    uniqueId: String,
    modifier: Modifier = Modifier,
    attachments: List<AttachmentEntity>? = null,
    defaultFontSize: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    contentBorderColor: Color = LocalCustomColors.current.webViewContentBorder,
    contentBgColor: Color = MaterialTheme.colorScheme.surface,
    quoteBgColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    codeBgColor: Color = LocalCustomColors.current.webViewCodeBg,
    onPageFinished: (() -> Unit)? = null
) {
    val tag = "LocalHtmlWebView"

    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val backgroundColorHex = remember { backgroundColor.toHexNoAlpha() }
    val textColorHex = remember { textColor.toHexNoAlpha() }
    val linkColorHex = remember { linkColor.toHexNoAlpha() }
    val contentBorderColorHex = remember { contentBorderColor.toHexNoAlpha() }
    val contentBgColorHex = remember { contentBgColor.toHexNoAlpha() }
    val quoteBgColorHex = remember { quoteBgColor.toHexNoAlpha() }
    val codeBgColorHex = remember { codeBgColor.toHexNoAlpha() }
    val styledHtmlContent = remember { mutableStateOf<String?>(null) }
    val imageViewerConfig = remember { ImageViewerConfig() }
    val loaded = rememberSaveable { mutableStateOf(false) }
    val nightMode = rememberSaveable { mutableStateOf(ThemeManager.isAppDarkMode) }

    LaunchedEffect(context) {
        scope.launchSafety {
            styledHtmlContent.value = withContext(Dispatchers.IO) {
                HtmlUtil.generateStyledHtmlContent(
                    content = when (format) {
                        100 -> {
                            content.toString()
                        }
                        2 -> {
                            HtmlUtil.markdownToHtml(
                                context,
                                content,
                                attachments,
                                imageViewerConfig
                            )
                        }
                        else -> {
                            HtmlUtil.bbcodeToHtml(
                                context,
                                content,
                                attachments,
                                imageViewerConfig,
                                defaultFontSize,
                                textColorHex,
                                backgroundColorHex
                            )
                        }
                    },
                    backgroundColor = backgroundColorHex,
                    textColor = textColorHex,
                    linkColor = linkColorHex,
                    contentBorderColor = contentBorderColorHex,
                    contentBgColor = contentBgColorHex,
                    quoteBgColor = quoteBgColorHex,
                    codeBgColor = codeBgColorHex,
                    defaultFontSize = defaultFontSize
                )
            }
        }
    }

    AndroidView(
        factory = {
            WebViewManager.getWebViewLayout(context, uniqueId).also { f ->
                (f.getChildAt(0) as? WebView?)?.apply {
                    setBackgroundColor(backgroundColorHex.toIntColor())
                    webViewClient = webViewClient(scope, navHostController, context, uriHandler, onPageFinished)
                    addJavascriptInterface(WebAppInterface(context, navHostController, imageViewerConfig), "Android")
                    if (!loaded.value && styledHtmlContent.value == null) {
                        loadDataWithBaseURL(Constants.BBS_URL, "", "text/html", "UTF-8", null)
                    }
                }
            }
        },
        update = {
            (it.getChildAt(0) as? WebView?)?.apply {
                setBackgroundColor(backgroundColorHex.toIntColor())
                if (nightMode.value != ThemeManager.isAppDarkMode) {
                    nightMode.value = ThemeManager.isAppDarkMode
                    loaded.value = false
                }

                if (!loaded.value && styledHtmlContent.value != null) {
                    loaded.value = true
                    loadDataWithBaseURL(Constants.BBS_URL, styledHtmlContent.value ?: "", "text/html", "UTF-8", null)
                }
            }
        },
        modifier = modifier
            .clipToBounds(), //why clipToBounds()? See: https://github.com/google/accompanist/issues/1442
    )
}

private class WebAppInterface(
    private val context: Context,
    val navHostController: NavHostController,
    val imageViewerConfig: ImageViewerConfig,
) {
    @JavascriptInterface
    fun onAttachmentClick(url: String?, name: String?) {
        (context as Activity).runOnUiThread {
            navHostController.navigate(Router.DownloadRouterEntity(
                name = name,
                url = url
            ))
        }
    }

    @JavascriptInterface
    fun onImageClick(url: String?) {
        if (url != null) {
            imageViewerConfig.images.forEachIndexed { index, imageViewerItem ->
                if (imageViewerItem.originUrl?.contains(url, true) == true
                    || imageViewerItem.thumbUrl?.contains(url, true) == true) {
                    imageViewerConfig.initialIndex = index
                }

                if (imageViewerItem.originUrl?.startsWith("/") == true) {
                    imageViewerItem.originUrl = imageViewerItem.originUrl.toBBSImgUrl()
                }
                if (imageViewerItem.thumbUrl?.startsWith("/") == true) {
                    imageViewerItem.thumbUrl = imageViewerItem.thumbUrl.toBBSImgUrl()
                }
            }

            (context as Activity).runOnUiThread {
                navHostController.navigate(
                    Router.ImageViewerRouterEntity(
                        config = ImageViewerConfig.toJson(imageViewerConfig)
                    )
                )
            }
        }
    }
}

private fun webViewClient(
    scope: CoroutineScope,
    navHostController: NavHostController,
    context: Context,
    uriHandler: UriHandler,
    onPageFinished: (() -> Unit)? = null
) = object : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        scope.launchSafety {
            delay(200)
            onPageFinished?.invoke()
        }
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (DataStore.ignoreSSL) {
            handler?.proceed()
        } else {
            super.onReceivedSslError(view, handler, error)
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val handle = linkNavigate(
            url = request?.url?.toString(),
            uriHandler = uriHandler,
            navHostController = navHostController
        )
        return true
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
//        return request?.url?.let { url ->
//            if (url.toString().endsWith(".jpg", true)
//                || url.toString().endsWith(".jpeg", true)
//                || url.toString().endsWith(".png", true)
//                || url.toString().endsWith(".gif", true)
//                || url.toString().endsWith(".heic", true)
//                || url.toString().endsWith(".heif", true)
//                || url.toString().endsWith(".webp", true)
//            ) {
//                return@let runBlocking {
//                    interceptRequest(context, url)
//                }
//            } else {
//                super.shouldInterceptRequest(view, request)
//            }
//        }
    }
}

private suspend fun interceptRequest(context: Context, uri: Uri): WebResourceResponse? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = context.imageLoader
            val coilRequest = ImageRequest.Builder(context)
                .data(uri.toString())
                .allowHardware(false)
                .build()

            val result = (loader.execute(coilRequest) as? SuccessResult)

            val inputStream = when (result) {
                is SuccessResult -> {
                    val outputStream = ByteArrayOutputStream()
                    result.drawable.toBitmap().compress(Bitmap.CompressFormat.PNG, 20, outputStream)
                    ByteArrayInputStream(outputStream.toByteArray())
                }
                else -> null
            }

            if (inputStream != null) {
                WebResourceResponse("image/jpeg", null, inputStream)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


