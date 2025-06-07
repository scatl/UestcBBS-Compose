package com.scatl.uestcbbs.compose.module.setting

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.getVersionName
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router

/**
 * Created by sca_tl at 2024/9/13 14:12:36
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutScreen() {

    val tag = "AboutScreen"
    val context = LocalContext.current
    val appIcon = remember { mutableStateOf<Drawable?>(null) }
    val uriHandler = LocalUriHandler.current
    val navHostController = LocalNavController.current

    LaunchedEffect(Unit) {
        appIcon.value = context.packageManager?.getApplicationIcon(context.packageName)
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about)
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(30.dp)
                            .clickable(unbound = true) {
                                navHostController.popBackStack()
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            appIcon.value?.toBitmap()?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .size(70.dp)
                )
            }

            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )

            Text(
                text = context.getVersionName(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = paddingValues.calculateBottomPadding()
                    ),
            ) {
                stickyHeader {
                    Text(
                        text = stringResource(R.string.author1),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .clipToBounds()
                            .wrapContentSize(align = Alignment.CenterStart)
                            .fillMaxWidth()
                            .clickable(unbound = true) {
                                navHostController.navigate(
                                    Router.UserProfileRouterEntity(
                                        uid = Constants.APP_AUTHOR_BBS_UID,
                                        name = Constants.APP_AUTHOR_BBS_NAME
                                    )
                                )
                            }
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 15.dp,
                                end = 15.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.about_bbs_account),
                            fontSize = 15.sp,
                        )
                        DspText(text = Constants.APP_AUTHOR_BBS_NAME)
                    }
                }

                stickyHeader {
                    Text(
                        text = stringResource(R.string.about_opensource_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .clipToBounds()
                            .wrapContentSize(align = Alignment.CenterStart)
                            .fillMaxWidth()
                            .clickable(unbound = true) {
                                uriHandler.openUri(Constants.APP_OPEN_SOURCE_GITHUB_URL)
                            }
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 15.dp,
                                end = 15.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.about_opensource_code),
                            fontSize = 15.sp,
                        )
                        DspText(text = Constants.APP_OPEN_SOURCE_GITHUB_URL)
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .clipToBounds()
                            .wrapContentSize(align = Alignment.CenterStart)
                            .fillMaxWidth()
                            .clickable(unbound = true) {
                                navHostController.navigate(Router.OpenSourceRouterEntity)
                            }
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 15.dp,
                                end = 15.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.about_opensource_license),
                            fontSize = 15.sp,
                        )
                        DspText(text = stringResource(R.string.about_opensource_license_dsp))
                    }
                }

                stickyHeader {
                    Text(
                        text = stringResource(R.string.about_feedback_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .clipToBounds()
                            .wrapContentSize(align = Alignment.CenterStart)
                            .fillMaxWidth()
                            .clickable(unbound = true) {
                                navHostController.navigate(
                                    Router.ChatDetailRouterEntity(
                                        uid = Constants.APP_AUTHOR_BBS_UID,
                                        name = Constants.APP_AUTHOR_BBS_NAME
                                    )
                                )
                            }
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 15.dp,
                                end = 15.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.about_feedback_pm),
                            fontSize = 15.sp,
                        )
                        DspText(text = stringResource(R.string.about_feedback_pm_dsp))
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .clipToBounds()
                            .wrapContentSize(align = Alignment.CenterStart)
                            .fillMaxWidth()
                            .clickable(unbound = true) {
                                runCatching {
                                    val data = Intent(Intent.ACTION_SENDTO)
                                    data.setData(Uri.parse("mailto:${Constants.APP_AUTHOR_EMAIL}"))
                                    context.startActivity(data)
                                }.onFailure {
                                    XLog.tag(tag).d(it)
                                }
                            }
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 15.dp,
                                end = 15.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.about_feedback_email),
                            fontSize = 15.sp,
                        )
                        DspText(text = stringResource(R.string.about_feedback_email_dsp))
                    }
                }
            }
        }
    }
}