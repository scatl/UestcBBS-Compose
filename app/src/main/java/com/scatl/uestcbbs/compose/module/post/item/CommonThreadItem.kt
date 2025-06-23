package com.scatl.uestcbbs.compose.module.post.item

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdsClick
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.dp2Sp
import com.scatl.uestcbbs.compose.ext.hexToColor
import com.scatl.uestcbbs.compose.ext.imageCorner
import com.scatl.uestcbbs.compose.ext.measureTextWidth
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toBBSImgUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.CommonIconNameView
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.NinePicGridLayout

/**
 * Created by sca_tl at 2024/7/21 17:20
 */
@Composable
fun CommonThreadItem (
    data: CommonThreadEntity?,
) {
    if (data == null) {
        return
    }

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
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
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

            if (!(data.favoriteTimes == 0 || data.favoriteTimes == null)) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Rounded.StarOutline,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = data.favoriteTimes.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        CommonThreadTitle(
            data = data,
            labelTextStyle = TextStyle(
                fontSize = 12.sp
            ),
            textStyle = TextStyle(
                fontSize = 17.sp
            )
        )

        if (!data.summary.isNullOrEmpty()) {
            Text(
                text = data.summary.toString(),
                modifier = Modifier.alpha(0.7f),
                fontSize = 14.sp,
            )
        }

        val images = data.summaryAttachments?.filter { it.isImage == 1 }
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
                    navHostController.navigate(
                        Router.ImageViewerRouterEntity(
                            config = ImageViewerConfig.toJson(
                                ImageViewerConfig(
                                    images = imageViewerItems,
                                    initialIndex = index
                                )
                            )
                        )
                    )
                }
            )
        }

        val attachments = data.summaryAttachments?.filter { it.isImage != 1 }
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

        Text(
            text = if (data.lastPost != null && !data.lastPoster.isNullOrEmpty()) {
                stringResource(
                    R.string.latest_reply_at_by,
                    data.lastPoster.toString(),
                    formatTimestamp(data.lastPost, LocalContext.current)
                )
            } else {
                ""
            },
            fontSize = 12.sp,
            lineHeight = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(0.5f)
        )

        if (data.displayForumName && (data.forumName.isNullOrEmpty().not() || data.forumId.toIntOrElse() > 0)) {
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
                    text = if (data.forumName.isNullOrEmpty().not()) {
                        data.forumName.toString()
                    } else {
                        ForumCategoryManager.getForum(data.forumId)?.name.toString()
                    },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = pagePadding + 5.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.5f)
                )
                Text(
                    text = data.recommendAdd.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ModeComment,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.5f)
                )
                Text(
                    text = data.replies.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdsClick,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .alpha(0.5f)
                )
                Text(
                    text = data.views.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                )
            }
        }
    }
}

@Composable
fun CommonThreadTitle(
    data: CommonThreadEntity?,
    textStyle: TextStyle,
    labelTextStyle: TextStyle,
    maxLines: Int = Int.MAX_VALUE
) {
    if (data == null) {
        return
    }

    val text = buildAnnotatedString {
        if (data.special == 1) {
            appendInlineContent(id = "vote")
        }
        if ((data.replyAward ?: 0) > 0) {
            appendInlineContent(id = "reply_award")
        }
        if (data.digest == 1) {
            appendInlineContent(id = "digest")
        }
        if (data.stamp == 3 || data.icon == 12) {
            appendInlineContent(id = "great")
        }
        if (data.stamp == 5 || data.icon == 14) {
            appendInlineContent(id = "recommend")
        }
        if (data.icon == 20) {
            appendInlineContent(id = "new_comer")
        }
        append(data.subject)
    }

    val inlineContent = mapOf(
        "vote" to InlineTextContent(
            placeholder = Placeholder(
                width = 22.sp,
                height = 22.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                Icon(
                    imageVector = Icons.Outlined.Poll,
                    contentDescription = null,
                    tint = LocalCustomColors.current.threadTitleVoteLabel,
                    modifier = Modifier.size(22.dp)
                )
            }
        ),

        "new_comer" to InlineTextContent(
            placeholder = Placeholder(
                width = (measureTextWidth(text = stringResource(id = R.string.new_comer), style = labelTextStyle) + 15.dp).dp2Sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                LabelText(
                    text = stringResource(id = R.string.new_comer),
                    textStyle = labelTextStyle,
                    colors = listOf(
                        LocalCustomColors.current.threadTitleNewComerStart,
                        LocalCustomColors.current.threadTitleNewComerEnd
                    )
                )
            }
        ),

        "digest" to InlineTextContent(
            placeholder = Placeholder(
                width = (measureTextWidth(text = stringResource(id = R.string.featured), style = labelTextStyle) + 15.dp).dp2Sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                LabelText(
                    text = stringResource(id = R.string.featured),
                    textStyle = labelTextStyle,
                    colors = listOf(
                        LocalCustomColors.current.threadTitleDigestStart,
                        LocalCustomColors.current.threadTitleDigestEnd
                    )
                )
            }
        ),

        "great" to InlineTextContent(
            placeholder = Placeholder(
                width = (measureTextWidth(text = stringResource(id = R.string.great), style = labelTextStyle) + 15.dp).dp2Sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                LabelText(
                    text = stringResource(id = R.string.great),
                    textStyle = labelTextStyle,
                    colors = listOf(
                        LocalCustomColors.current.threadTitleGreatStart,
                        LocalCustomColors.current.threadTitleGreatEnd
                    )
                )
            }
        ),

        "recommend" to InlineTextContent(
            placeholder = Placeholder(
                width = (measureTextWidth(text = stringResource(id = R.string.recommend), style = labelTextStyle) + 15.dp).dp2Sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                LabelText(
                    text = stringResource(id = R.string.recommend),
                    textStyle = labelTextStyle,
                    colors = listOf(
                        LocalCustomColors.current.threadTitleRecommendStart,
                        LocalCustomColors.current.threadTitleRecommendEnd
                    )
                )
            }
        ),

        "reply_award" to InlineTextContent(
            placeholder = Placeholder(
                width = (measureTextWidth(text = stringResource(id = R.string.reply_award).plus(" ${data.replyAward}"), style = labelTextStyle) + 15.dp).dp2Sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                LabelText(
                    text = stringResource(id = R.string.reply_award).plus(" ${data.replyAward}"),
                    textStyle = labelTextStyle,
                    colors = listOf(
                        LocalCustomColors.current.threadTitleReplyAwardStart,
                        LocalCustomColors.current.threadTitleReplyAwardEnd
                    )
                )
            }
        ),
    )

    Text(
        text = text,
        inlineContent = inlineContent,
        style = textStyle,
        modifier = Modifier.alpha(0.9f),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textDecoration = if (data.highLightUnderline == true) TextDecoration.Underline else TextDecoration.None,
        fontStyle = if (data.highLightItalic == true) FontStyle.Italic else FontStyle.Normal,
        fontWeight = if (data.highLightBold == true) FontWeight.Bold else FontWeight.Normal,
        color = data.highLightColor.hexToColor(default = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
private fun LabelText(
    text: String,
    textStyle: TextStyle,
    colors: List<Color>
) {
    Text(
        text = text,
        style = textStyle,
        color = Color.White,
        modifier = Modifier
            .height(20.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 100f),
                    end = Offset(100f, 0f)
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 5.dp)
            .padding(top = 1.dp)
    )
}
