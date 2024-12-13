package com.scatl.uestcbbs.compose.module.auth

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hcaptcha.sdk.HCaptchaConfig
import com.hcaptcha.sdk.HCaptchaException
import com.hcaptcha.sdk.HCaptchaResponse
import com.hcaptcha.sdk.HCaptchaWebView
import com.hcaptcha.sdk.HCaptchaWebViewHelper
import com.hcaptcha.sdk.IHCaptchaVerifier
import com.scatl.uestcbbs.compose.R

@Composable
fun HCaptchaDialog(
    showDialog: Boolean,
    config: HCaptchaConfig,
    onDismissRequest: () -> Unit,
    onResult: (HCaptchaResponse) -> Unit
) {
    if (showDialog) {
        var showLoading by remember { mutableStateOf(true) }

        val handler = Handler(Looper.getMainLooper())
        val verifier = object : IHCaptchaVerifier {
            override fun onLoaded() {
                onResult(HCaptchaResponse.Loaded)
            }

            override fun onOpen() {
                showLoading = false
                onResult(HCaptchaResponse.Open)
            }

            override fun onSuccess(result: String) {
                onResult(HCaptchaResponse.Success(result))
            }

            override fun onFailure(exception: HCaptchaException) {
                onResult(HCaptchaResponse.Failure(exception.hCaptchaError))
            }
        }

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (showLoading) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 30.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_account_captcha_loading),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        HCaptchaWebView(context).apply {
                            HCaptchaWebViewHelper(
                                handler,
                                context,
                                config,
                                verifier,
                                this
                            )
                        }
                    }
                )
            }
        }
    }
}