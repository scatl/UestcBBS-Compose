package com.scatl.uestcbbs.compose.module.home.newpost.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.hexToColor
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.module.home.newpost.entity.SiteStatusData
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.LoopBanner
import kotlin.math.max

/**
 * Created by sca_tl at 2024/7/11 14:39:10
 */
@Composable
fun NewThreadSiteStatus(
    data: SiteStatusData
) {
    val uriHandler = LocalUriHandler.current
    val navHostController = LocalNavController.current
    val titleHeight = rememberSaveable { mutableIntStateOf(200) }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg()
    ) {
        IconTitle(
            icon = Icons.Outlined.Poll,
            iconSize = 18.dp,
            text = stringResource(R.string.site_statistics),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = stringResource(
                R.string.site_statistics_posters,
                data.onlineNum.toString(),
                data.indexEntity.globalStat?.todayPosts.toString(),
                data.indexEntity.globalStat?.yesterdayPosts.toString(),
                data.indexEntity.globalStat?.totalPosts.toString(),
                data.indexEntity.globalStat?.totalUsers.toString()
            ),
            fontSize = 13.sp,
            maxLines = 1,
            modifier = Modifier
                .alpha(alpha = 0.7f)
                .horizontalScroll(rememberScrollState())
                //.basicMarquee()
        )

        if (data.indexEntity.announcement.isNotEmpty()) {
            Spacer(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainer))
            Spacer(modifier = Modifier.height(1.dp))
            IconTitle(
                icon = Icons.AutoMirrored.Outlined.VolumeUp,
                iconSize = 18.dp,
                text = stringResource(R.string.announcement),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            LoopBanner(
                originDataSize = data.indexEntity.announcement.size,
                vertical = true,
                userScrollEnabled = false,
                modifier = Modifier
                    .heightIn(min = 1.dp, max = titleHeight.intValue.px2dp),
                pageContent = { dataIndex, _ ->
                    Text(
                        text = "(${dataIndex + 1}/${data.indexEntity.announcement.size}) " + data.indexEntity.announcement.getOrNull(dataIndex)?.title.toString(),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = data.indexEntity.announcement.getOrNull(dataIndex)?.highlightColor.hexToColor(LocalContentColor.current),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                linkNavigate(
                                    url = data.indexEntity.announcement.getOrNull(dataIndex)?.href,
                                    uriHandler = uriHandler,
                                    navHostController = navHostController
                                )
                            }
                            .onSizeChanged {
                                titleHeight.intValue = it.height
                            }
                    )
                }
            )
        }
    }
}