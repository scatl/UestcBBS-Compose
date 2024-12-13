package com.scatl.uestcbbs.compose.module.user.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.IconTitle

/**
 * Created by sca_tl at 2024/7/21 18:32
 */
@Composable
fun UserReplyItem(
    data: CommonThreadEntity,
    reply: Boolean = true
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
    ) {
        Text(
            text = data.subject.toString(),
            modifier = Modifier.alpha(0.9f),
            fontSize = 17.sp
        )

        if (!data.summaries.isNullOrEmpty()) {
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
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    iconSize = 18.dp,
                    text = stringResource(id = if (reply) R.string.user_his_replies else R.string.user_his_comment),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                data.summaries?.forEachIndexed { index, summary ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                            ) {
                                append((index + 1).toString())
                            }
                            append(":${summary}")
                        },
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (data.lastPost != null && !data.lastPoster.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(
                    R.string.latest_reply_at_by,
                    data.lastPoster.toString(),
                    formatTimestamp(data.lastPost, LocalContext.current)
                ),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        Row (
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!data.forumName.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(vertical = 3.dp, horizontal = 4.dp)
                ) {
                    Text(
                        text = data.forumName.toString(),
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                }
            }

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