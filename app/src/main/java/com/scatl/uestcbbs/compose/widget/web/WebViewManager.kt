package com.scatl.uestcbbs.compose.widget.web

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout

/**
 * Created by sca_tl at 2024/8/27 15:12:10
 */
object WebViewManager {

    private const val MAX_CAPACITY = 20

    private val webViewMap = object : LinkedHashMap<String, FrameLayout>(MAX_CAPACITY, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, FrameLayout>): Boolean {
            return size > MAX_CAPACITY
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Synchronized
    fun getWebViewLayout(context: Context, id: String): FrameLayout {
        return webViewMap.getOrPut(id) {
            val webView = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                isNestedScrollingEnabled = true

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }

            (webView.parent as? ViewGroup?)?.removeAllViews()

            //FrameLayout的原因：Fatal signal 11
            FrameLayout(context).apply {
                removeAllViews()
                clipToOutline = true
                addView(webView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
        }
    }

}