package com.scatl.uestcbbs.compose.module.home.toplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.imageCorner
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toBBSImgUrl
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.NinePicGridLayout
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.HtmlUtil
import com.scatl.uestcbbs.compose.widget.CommonIconNameView

/**
 * Created by sca_tl at 2024/7/11 18:38:26
 */
@Composable
fun ItemTopListPost(
    data: CommonThreadEntity
) {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg {
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = data.threadId.toIntOrElse()
                    )
                )
            }
            //.randomBg()
    ) {
        CommonIconNameView(
            iconUrl = data.authorId.toAvatarUrl(),
            name = data.author.toString(),
            date = data.dateline
        ) {
            navHostController.navigate(
                Router.UserProfileRouterEntity(
                    uid = data.authorId,
                    name = data.author.toString()
                )
            )
        }

        Text(
            text = data.subject.toString(),
            modifier = Modifier.alpha(0.9f),
            fontSize = 17.sp
        )

        if (!data.summary.isNullOrEmpty()) {
            Text(
                text = data.summary.toString(),
                modifier = Modifier.alpha(0.7f),
                fontSize = 14.sp,
            )
        }

        val images = data.summaryAttachments?.filter { it.isImage == 1 || HtmlUtil.isImage(it.filename)}
        if (!images.isNullOrEmpty()) {
            val imageViewerItems = mutableListOf<ImageViewerConfig.ImageViewerItem>()
            images.forEach {
                imageViewerItems.add(
                    ImageViewerConfig.ImageViewerItem(
                        originUrl = it.path.toBBSImgUrl(),
                        thumbUrl = it.thumbnailUrl.toBBSImgUrl()
                    )
                )
            }

            NinePicGridLayout(
                images = imageViewerItems,
                modifier = Modifier.fillMaxWidth(),
                gridSpace = 4.dp,
                imageCorner = imageCorner,
                onClick = { index, entities ->
                    val imageViewerConfig = ImageViewerConfig(
                        images = imageViewerItems,
                        initialIndex = index
                    )
                    val configId = ImageViewerConfig.saveConfig(imageViewerConfig)
                    navHostController.navigate(Router.ImageViewerRouterEntity(configId))
                }
            )
        }

        val attachments = data.summaryAttachments?.filter { it.isImage != 1 || !HtmlUtil.isImage(it.filename)}
        if (!attachments.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(cardCorner)
                    )
                    .padding(pagePadding)
            ) {
                IconTitle(
                    icon = Icons.Outlined.Attachment,
                    iconSize = 18.dp,
                    text = stringResource(R.string.attachment),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                attachments.forEachIndexed { index, summaryAttachment ->
                    Text(
                        text = "${index + 1}: ${summaryAttachment.filename}",
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(unbound = false) {
                    navHostController.navigate(
                        Router.ForumDetailRouterEntity(
                            fid = data.forumId.toIntOrElse()
                        )
                    )
                }
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_topic),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = ForumCategoryManager.getForum(data.forumId)?.name.toString(),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.latest_reply_at, formatTimestamp(data.lastPost, LocalContext.current)),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.alpha(0.5f)
            )
            Row {
                Text(
                    text = "${data.replies} ${stringResource(R.string.reply)}",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${data.views} ${stringResource(R.string.views)}",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}