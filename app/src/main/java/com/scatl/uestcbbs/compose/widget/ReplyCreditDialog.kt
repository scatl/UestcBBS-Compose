package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.scatl.uestcbbs.compose.api.entity.ThreadReplyEntity
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.theme.LocalCustomColors

/**
 * Created by sca_tl at 2024/11/14 15:48:33
 */
@Composable
fun ReplyCreditDialog(
    showDialog: MutableState<Boolean>,
    data: ThreadReplyEntity.ExtCreditsUpdate?
) {
    if (showDialog.value) {
        val lottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/gift.json"))

        Dialog(
            onDismissRequest = {
                showDialog.value = false
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 30.dp)
            ) {
                Text(
                    text = "恭喜中奖",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                LottieAnimation(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(150.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "回帖奖励：".plus(
                        if (data?.water.toIntOrElse(-1) > 0) {
                            data?.water.toString().plus("水滴").plus(" ")
                        } else {
                            ""
                        }
                    ).plus(
                        if (data?.prestige.toIntOrElse(-1) > 0) {
                            data?.prestige.toString().plus("威望").plus(" ")
                        } else {
                            ""
                        }
                    ).plus(
                        if (data?.coupons.toIntOrElse(-1) > 0) {
                            data?.coupons.toString().plus("奖励券").plus(" ")
                        } else {
                            ""
                        }
                    ),
                    fontSize = 15.sp,
                    color = LocalCustomColors.current.threadDetailReplyAward,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}