package com.scatl.uestcbbs.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty

/**
 * Created by sca_tl at 2024/9/19 15:21:47
 */
@Composable
fun Tip (
    tip: String,
    tipId: String,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.tip),
    confirmText: String = stringResource(R.string.tip_no_show_again),
    bgColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    val show = rememberSaveable { mutableStateOf(!DataStore.tipShowedId.contains(tipId)) }
    AnimatedVisibility(
        visible = show.value,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column (
            modifier = Modifier
                .animateContentSize()
                .commonCardBg (
                    bgColor = bgColor,
                    containerColor = containerColor
                ) { }
        ) {
            IconTitle(
                icon = Icons.Outlined.TipsAndUpdates,
                iconTint = MaterialTheme.colorScheme.primary,
                text = title,
                iconSize = 20.dp,
                gap = 5.dp,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = tip,
                fontSize = 14.sp
            )
            if (confirmText.isNotNullAndEmpty()) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable(unbound = true) {
                            show.value = false
                            val value = mutableSetOf<String>().apply {
                                addAll(DataStore.tipShowedId)
                                add(tipId)
                            }
                            DataStore.tipShowedId = value
                        }
                )
            }
        }
    }
}

const val TIP_ID_THREAD_SNAPSHOT = "TIP_ID_THREAD_SNAPSHOT"
const val TIP_ID_MAGIC_SHOP = "TIP_ID_MAGIC_SHOP"
const val TIP_ID_RATE = "TIP_ID_RATE"