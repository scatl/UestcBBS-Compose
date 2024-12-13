package com.scatl.uestcbbs.compose.module.history.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.CommonIconNameView

/**
 * Created by sca_tl at 2024/9/10 20:45:39
 */
@Composable
fun BrowsingHistoryItem(
    item: BrowsingHistoryDBEntity
) {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg {
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = item.threadId.toIntOrElse()
                    )
                )
            }
    ) {
        CommonIconNameView(
            iconUrl = item.authorId.toIntOrElse().toAvatarUrl(),
            name = if (item.authorId.toIntOrElse() == 0) stringResource(R.string.anonymous) else item.authorName.toString(),
            date = item.dateLine
        ) {
            navHostController.navigate(
                Router.UserProfileRouterEntity(
                    uid = item.authorId.toIntOrElse(),
                    name = item.authorName.toString()
                )
            )
        }

        Text(
            text = item.subject.toString(),
            modifier = Modifier.alpha(0.9f),
            fontSize = 17.sp
        )

        Text(
            text = item.summary.toString().trim(),
            modifier = Modifier.alpha(0.6f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = item.forumName.toString(),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .alpha(0.5f)
                    .clickable(unbound = true) {
                        navHostController.navigate(
                            Router.ForumDetailRouterEntity(
                                fid = item.forumId.toIntOrElse()
                            )
                        )
                    }
            )

            Text(
                text = stringResource(R.string.browsing_history_last_time, formatTimestamp(((item.lastBrowserDate ?: 0) / 1000).toInt(), LocalContext.current)),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.alpha(0.5f)
            )
        }

    }
}