package com.scatl.uestcbbs.compose.module.message.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.api.entity.message.ChatDetailEntity
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp

/**
 * Created by sca_tl at 2024/8/22 10:34:16
 */
@Composable
fun ChatDetailItem(
    data: ChatDetailEntity.Row,
) {
    if (AccountManager.getSignedInAccount()?.uid == data.authorId.toString()) {
        RightMsg(
            data = data
        )
    } else {
        LeftMsg(
            data = data
        )
    }
}

@Composable
private fun LeftMsg(
    data: ChatDetailEntity.Row
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            AsyncImage(
                model = data.authorId.toAvatarUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(50))
                    .clickable {
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = data.authorId,
                                name = data.author.toString()
                            )
                        )
                    }
            )
            Column (
                modifier = Modifier
                    .padding(top = 5.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = cardCorner * 2,
                            bottomStart = cardCorner * 2,
                            bottomEnd = cardCorner * 2
                        )
                    )
                    .padding(pagePadding)
            ) {
                Text(
                    text = data.message.toString(),
                    fontSize = 15.sp,
                )
                Text(
                    text = formatTimestamp(data.dateline, context),
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}

@Composable
private fun RightMsg(
    data: ChatDetailEntity.Row
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Column (
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(top = 5.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(
                            topStart = cardCorner * 2,
                            topEnd = 0.dp,
                            bottomStart = cardCorner * 2,
                            bottomEnd = cardCorner * 2
                        )
                    )
                    .padding(pagePadding)
            ) {
                Text(
                    text = data.message.toString(),
                    fontSize = 15.sp,
                )
                Text(
                    text = formatTimestamp(data.dateline, context),
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }

            AsyncImage(
                model = data.authorId.toAvatarUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(50))
                    .clickable {
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = data.authorId,
                                name = data.author.toString()
                            )
                        )
                    }
            )
        }
    }

}