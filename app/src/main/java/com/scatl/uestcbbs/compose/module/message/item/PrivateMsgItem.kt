package com.scatl.uestcbbs.compose.module.message.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.formatTimestamp

/**
 * Created by sca_tl at 2024/7/31 17:31:03
 */
@Composable
fun PrivateMsgItem(
    data: MessageEntity.Row
) {
    val navHostController = LocalNavController.current
    val unread = remember { mutableStateOf(data.unread) }

    Column (
        modifier = Modifier
            .clickable(unbound = false) {
                navHostController.navigate(
                    Router.ChatDetailRouterEntity(
                        uid = data.toUid.toIntOrElse(),
                        name = data.toUsername.toString()
                    )
                )
            }
            .padding(horizontal = pagePadding)
            .padding(top = pagePadding)
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(pagePadding),
            modifier = Modifier
                .background(color = Color.Transparent)
        ) {
            AsyncImage(
                model = data.toUid?.toAvatarUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(shape = RoundedCornerShape(50))
                    .clickable(enabled = true) {
                        unread.value = false
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = data.toUid,
                                name = data.toUsername.toString()
                            )
                        )
                    }
            )
            Column {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (unread.value == true) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = RoundedCornerShape(50)
                                )
                                .size(8.dp)
                        )
                    }

                    Text(
                        text = data.toUsername.toString(),
                        fontSize = 15.sp,
                    )
                }

                Text(
                    text = data.lastSummary.toString().ifEmpty { "[图片]" },
                    fontSize = 14.sp,
                    //fontWeight = if (data.unread == true) FontWeight.Bold else FontWeight.Normal,
                    //color = if (data.unread == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.alpha(alpha = if (data.unread == true) 1f else 0.7f)
                )

                Text(
                    text = formatTimestamp(data.lastDateline, LocalContext.current),
                    fontSize = 13.sp,
                    modifier = Modifier.alpha(alpha = 0.4f)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            thickness = 0.2.dp,
            modifier = Modifier.padding(start = 55.dp)
        )
    }
}