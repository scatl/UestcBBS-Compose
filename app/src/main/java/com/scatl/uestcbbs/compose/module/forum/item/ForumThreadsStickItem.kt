package com.scatl.uestcbbs.compose.module.forum.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadTitle
import com.scatl.uestcbbs.compose.router.LocalNavController

/**
 * Created by sca_tl at 2024/7/25 10:26:56
 */
@Composable
fun ForumThreadsStickItem (
    data: List<CommonThreadEntity>
) {
    if (data.isEmpty()) {
        return
    }
    val navHostController = LocalNavController.current
    val isExpanded = rememberSaveable { mutableStateOf(DataStore.expandStickPost) }
    val arrowRotated = rememberSaveable { mutableStateOf(DataStore.expandStickPost) }
    val latestStick = data.filter { System.currentTimeMillis() - it.dateline.toIntOrElse() * 1000L <= 30L * Constants.DAY_MILLIS }

    Column {
        Column {
//            if (latestStick.isNotEmpty()) {
//                Column (
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(pagePadding)
//                ) {
//                    Text(
//                        text = "最近置顶",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 14.sp
//                    )
//                    latestStick.forEachIndexed { index, commonThreadEntity ->
//                        Row (
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(5.dp),
//                            modifier = Modifier
//                                .padding(top = 10.dp)
//                        ) {
//                            AsyncImage(
//                                model = commonThreadEntity.authorId.toAvatarUrl(),
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .size(20.dp)
//                                    .clip(shape = RoundedCornerShape(50))
//                                    .clickable {
//
//                                    }
//                            )
//                            CommonThreadTitle(
//                                data = commonThreadEntity,
//                                maxLines = 1,
//                                labelTextStyle = TextStyle(
//                                    fontSize = 12.sp
//                                ),
//                                textStyle = TextStyle(
//                                    fontSize = 17.sp
//                                )
//                            )
//                        }
//                    }
//                }
//            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        arrowRotated.value = !arrowRotated.value
                        isExpanded.value = !isExpanded.value
                    }
                    .padding(pagePadding)
            ) {
                Text(
                    text = LocalContext.current.getString(if (isExpanded.value) {
                        R.string.forum_detail_stick_title_show
                    } else {
                        R.string.forum_detail_stick_title_hide
                    }, data.size),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(animateFloatAsState(
                        targetValue = if (arrowRotated.value) 180f else 0f,
                        animationSpec = tween(durationMillis = 500),
                        label = "arrow_rotation"
                    ).value)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded.value) {
            Column  {
                data.forEachIndexed { index, commonPostEntity ->
                    CommonThreadItem(data = commonPostEntity)
                }
            }
        }
    }

}