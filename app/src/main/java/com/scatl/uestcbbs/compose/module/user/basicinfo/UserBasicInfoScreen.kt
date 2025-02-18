package com.scatl.uestcbbs.compose.module.user.basicinfo

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toBBSImgUrl
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.util.calculateDays
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.user.UserProfilePage
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.web.LocalHtmlWebView
import java.util.UUID

/**
 * Created by sca_tl at 2024/7/17 10:53:42
 */
@Composable
fun UserBasicInfoScreen(
    data: UserProfileEntity,
    viewModel: UserViewModel,
    state: ScrollState = rememberScrollState()
) {
    val hidePrivateInfo = rememberSaveable { mutableStateOf(true) }
    viewModel.setPageInitialized(UserProfilePage.HOME)

    Column (
        verticalArrangement = Arrangement.spacedBy(pagePadding),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = state)
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = pagePadding)
            .padding(top = pagePadding)
    ) {
        Column (
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
                .fillMaxSize()
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.user_basic_profile),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                IconTitle(
                    icon = if (hidePrivateInfo.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    iconSize = 18.dp,
                    text = if (hidePrivateInfo.value) "展示所有信息" else "隐藏敏感信息",
                    textStyle = TextStyle(
                        fontSize = 15.sp
                    ),
                    modifier = Modifier
                        .alpha(alpha = 0.7f)
                        .clickable(unbound = true) {
                            hidePrivateInfo.value = hidePrivateInfo.value.not()
                        }
                )
            }
            Spacer(modifier = Modifier.height(pagePadding))
            Text(
                text = "UID：${data.userSummary?.uid}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "${stringResource(id = R.string.user_group)}：${data.userSummary?.groupTitle}",
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                AsyncImage(
                    model = data.userSummary?.groupIcon?.toBBSImgUrl(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(25.dp)
                )
            }
            Text(
                text = "${stringResource(id = R.string.user_level)}：${data.userSummary?.levelId}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            if (!data.customTitle.removeAllBlank().isNullOrEmpty()) {
                Row {
                    Text(
                        text = "${stringResource(id = R.string.user_custom_title)}：",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = "${data.customTitle}",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                }
            }
            if (!data.introduction.removeAllBlank().isNullOrEmpty()) {
                Row {
                    Text(
                        text = "${stringResource(id = R.string.user_introduction)}：",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = "${data.introduction.removeAllBlank()}",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                }
            }

            if (!data.email.removeAllBlank().isNullOrEmpty()) {
                Row {
                    Text(
                        text = "${stringResource(id = R.string.email)}：",
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = if (hidePrivateInfo.value) "******" else "${data.email.removeAllBlank()}",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .animateContentSize()
                            .alpha(0.7f)
                    )
                }
            }
        }

        if (!data.signature?.removeAllBlank().isNullOrEmpty()) {
            Column (
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(cardCorner)
                    )
                    .padding(pagePadding)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.signature),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(pagePadding))

                LocalHtmlWebView(
                    content = data.signature,
                    format = 100,
                    defaultFontSize = 14,
                    uniqueId = data.userSummary?.uid.toString(),
                    backgroundColor = MaterialTheme.colorScheme.surface
                )
            }
        }

        Column (
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.user_activity_profile),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(pagePadding))
            Text(
                text = "${stringResource(id = R.string.user_online_time)}：${data.onlineTime} ${stringResource(id = R.string.hour)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = "${stringResource(id = R.string.user_registration_time)}：${formatTimestamp(data.registerTime, LocalContext.current)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            if (!data.registerIp.isNullOrEmpty()) {
                Text(
                    text = "${stringResource(id = R.string.user_registration_ip)}：${if (hidePrivateInfo.value) "******" else data.registerIp}",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .animateContentSize()
                        .alpha(0.7f)
                )
            }
            Text(
                text = "${stringResource(id = R.string.user_last_visit_time)}：${formatTimestamp(data.lastVisit, LocalContext.current)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = "${stringResource(id = R.string.user_last_activity)}：${formatTimestamp(data.lastActivity, LocalContext.current)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = "${stringResource(id = R.string.user_last_post)}：${formatTimestamp(data.lastPost, LocalContext.current)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
            if (!data.lastIp.isNullOrEmpty()) {
                Text(
                    text = "${stringResource(id = R.string.user_last_ip)}：${if (hidePrivateInfo.value) "******" else data.lastIp}",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .animateContentSize()
                        .alpha(0.7f)
                )
            }
            Text(
                text = "${stringResource(id = R.string.user_registration_days)}：${calculateDays(data.registerTime)} ${stringResource(id = R.string.day)}",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }

        if (!data.userSummary?.medals.isNullOrEmpty()) {
            Box (
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(cardCorner)
                    )
                    .padding(pagePadding)
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkspacePremium,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(100.dp)
                        .alpha(0.08f)
                )
                Column {
                    Text(
                        text = stringResource(id = R.string.medal),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(pagePadding))
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        data.userSummary?.medals?.forEachIndexed { index, i ->
                            val entity = viewModel.userRepository.dataBase.getMedalDao().findFirstById(i)
                            if (entity != null) {
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    AsyncImage(
                                        model = entity.image,
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Column (
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = entity.name.toString(),
                                            fontSize = 13.sp,
                                            lineHeight = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = entity.dsp.toString(),
                                            fontSize = 11.sp,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.alpha(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Visitors(
            data = data,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun Visitors(
    data: UserProfileEntity,
    viewModel: UserViewModel,
) {
    val navHostController = LocalNavController.current
    val signedInAccount = viewModel.userRepository.dataBase.getAccountDao().getSignedInAccount()

    if (!data.recentVisitors.isNullOrEmpty()) {
        Column (
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.user_recent_visitors),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = stringResource(id = R.string.user_space_views, data.userSummary?.views.toString()),
                fontSize = 13.sp,
                modifier = Modifier.alpha(0.6f)
            )
            Spacer(modifier = Modifier.height(pagePadding))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(70.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp, max = 1000.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                state = rememberLazyGridState(),
                userScrollEnabled = false
            ) {
                itemsIndexed(
                    items = data.recentVisitors,
                    key = { index, item ->
                        item.toString()
                    }
                ) { index, item ->
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .animateItem()
                            .clickable(unbound = true) {
                                navHostController.navigate(
                                    Router.UserProfileRouterEntity(
                                        uid = item.uid,
                                        name = item.username.toString()
                                    )
                                )
                            }
                    ) {
                        Box {
                            AsyncImage(
                                model = item.uid.toAvatarUrl(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            if (signedInAccount?.uid.isNotNullAndEmpty() && signedInAccount?.uid == item.uid.toString()) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 10.dp)
                                        .clickable(unbound = true) {
                                            viewModel.removeLog()
                                        }
                                )
                            }
                        }

                        Text(
                            text = item.username.toString(),
                            fontSize = 11.sp,
                            lineHeight = 11.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(70.dp)
                        )
                        Text(
                            text = formatTimestamp(item.dateline, LocalContext.current),
                            fontSize = 9.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(70.dp)
                        )
                    }
                }
            }
        }
    }
}