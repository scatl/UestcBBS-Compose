package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.scatl.uestcbbs.compose.ext.clickable

/**
 * Created by sca_tl at 2025/7/1 18:34:55
 */
@Composable
fun LottieDialog(
    showDialog: MutableState<Boolean>,
    lottieFileName: String,
    text: String,
    confirmText: String? = null,
    cancelText: String? = null,
    cancelable: Boolean = false,
    onConfirmClick: (() -> Unit)? = null,
    onCancelClick: (() -> Unit)? = null
) {
    if (showDialog.value) {
        val lottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/${lottieFileName}.json"))

        Dialog(
            onDismissRequest = {
                showDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = if (confirmText.isNullOrEmpty() && cancelText.isNullOrEmpty()) true else cancelable,
                dismissOnClickOutside = if (confirmText.isNullOrEmpty() && cancelText.isNullOrEmpty()) true else cancelable,
            )
        ) {
            Box (
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(top = 110.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    )

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(30.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp)
                    ) {
                        if (cancelText.isNullOrEmpty().not()) {
                            Text(
                                text = cancelText.toString(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable(unbound = true) {
                                        onCancelClick?.invoke()
                                    }
                            )
                        }

                        if (confirmText.isNullOrEmpty().not()) {
                            Text(
                                text = confirmText.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable(unbound = true) {
                                        onConfirmClick?.invoke()
                                    }
                            )
                        }
                    }
                }

                LottieAnimation(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(200.dp)
                )
            }
        }
    }
}