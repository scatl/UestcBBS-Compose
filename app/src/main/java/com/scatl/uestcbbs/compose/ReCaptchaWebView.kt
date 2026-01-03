package com.scatl.uestcbbs.compose

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Created by tanlei02 at 2025/12/24 9:32:14
 */
@Composable
fun ReCaptchaWebView(
    siteKey: String,
    onVerificationSuccess: (String) -> Unit,
    onVerificationFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    //userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
                    domStorageEnabled = true
                }

                webViewClient = object : WebViewClient() {
                    private var isInjected = false

                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (!isInjected) {
                            view?.loadUrl("""
                                javascript:
                                grecaptcha.render('recaptcha', {
                                    'sitekey': '$siteKey',
                                    'callback': function(token) {
                                        Android.onVerificationSuccess(token);
                                    },
                                    'error-callback': function() {
                                        Android.onVerificationFailed();
                                    },
                                    'expired-callback': function() {
                                        Android.onVerificationFailed();
                                    }
                                });
                            """.trimIndent())
                            isInjected = true
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        onVerificationFailed() // 网络错误时触发失败
                    }
                }
                addJavascriptInterface(VerifiedHandler(onVerificationSuccess, onVerificationFailed), "Android")
                loadDataWithBaseURL("https://recaptcha.net", generateRecaptchaHtml(), "text/html", "UTF-8", null)
                webView = this
            }
        }
    )
}

private fun generateRecaptchaHtml(): String {
    return """
        <!DOCTYPE html>
        <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script src="https://recaptcha.net/recaptcha/api.js" async defer></script>
                <script>
                    var onloadcallback = function() {
                        console.log('reCAPTCHA loaded');
                    };
                </script>
            </head>
            <body style="margin:0; display:flex; justify-content:center;">
                <div id="recaptcha"></div>
            </body>
        </html>
    """.trimIndent()
}

private class VerifiedHandler(
    private val onVerificationSuccess: (String) -> Unit,
    private val onVerificationFailed: () -> Unit
) {
    @JavascriptInterface
    fun onVerificationSuccess(token: String) {
        if (token.isNotEmpty()) {
            Handler(Looper.getMainLooper()).post {
                onVerificationSuccess.invoke(token)
            }
        }
    }

    @JavascriptInterface
    fun onVerificationFailed() {
        Handler(Looper.getMainLooper()).post {
            onVerificationFailed.invoke()
        }
    }
}

