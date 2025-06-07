package com.scatl.uestcbbs.compose.module.wealth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.StatusLayout
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2025/6/5 17:45:08
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWealthScreen() {
    val tag = "MyWealthScreen"
    val viewModel: MyWealthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val baseWealthData by viewModel.baseData.collectAsStateWithLifecycle()

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(300)
            viewModel.getBaseWealthData()
        }
    }

    Scaffold (
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "我的财富"
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(30.dp)
                            .unboundClickable {
                                navHostController.popBackStack()
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            StatusLayout(
                uiState = baseWealthData,
                onRetry = {
                    viewModel.getBaseWealthData()
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                        .padding(paddingValues)
                        .padding(15.dp)
                ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .padding(pagePadding)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("水滴: ".plus(baseWealthData.data?.waterCount))
                                    addStyle(
                                        style = SpanStyle(
                                            fontFamily = FontFamily.Cursive,
                                            fontWeight = FontWeight.W900
                                        ),
                                        start = 4,
                                        end = 4 + baseWealthData.data?.waterCount.toString().length
                                    )
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "水滴可以用来购买道具和勋章，给用户评分。你可以通过发表优质帖子，进行水滴任务，参加河畔活动或者购买刮刮卡来获取水滴（如果你运气好的话）。",
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .padding(pagePadding)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("积分: ".plus(baseWealthData.data?.creditCount))
                                    addStyle(
                                        style = SpanStyle(
                                            fontFamily = FontFamily.Cursive,
                                            fontWeight = FontWeight.W900
                                        ),
                                        start = 4,
                                        end = 4 + baseWealthData.data?.creditCount.toString().length
                                    )
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "你可以通过发帖，增加在线时间来获取积分，积分可帮助你提升河畔等级。积分计算方法：${baseWealthData.data?.creditRule}",
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                        }
                    }

                    Row (
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .padding(pagePadding)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("威望: ".plus(baseWealthData.data?.prestigeCount))
                                    addStyle(
                                        style = SpanStyle(
                                            fontFamily = FontFamily.Cursive,
                                            fontWeight = FontWeight.W900
                                        ),
                                        start = 4,
                                        end = 4 + baseWealthData.data?.prestigeCount.toString().length
                                    )
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "你可以通过发表精华帖或者做大红楼任务获取威望。威望可以兑换成水滴、购买勋章以及提升积分。",
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .padding(pagePadding)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("奖励券: ".plus(baseWealthData.data?.couponCount))
                                    addStyle(
                                        style = SpanStyle(
                                            fontFamily = FontFamily.Cursive,
                                            fontWeight = FontWeight.W900
                                        ),
                                        start = 5,
                                        end = 5 + baseWealthData.data?.couponCount.toString().length
                                    )
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "你可以通过发表精华帖或者用水滴兑换成奖励券。奖励券购买勋章。",
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "相关功能",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FunctionItem(
                            icon = R.drawable.ic_water_task,
                            name = "水滴小任务"
                        ) {
                            navHostController.navigate(Router.WaterTaskRouterEntity)
                        }

                        FunctionItem(
                            icon = R.drawable.ic_money_transfer,
                            name = "水滴转账"
                        ) {

                        }

                        FunctionItem(
                            icon = R.drawable.ic_money_transfer,
                            name = "水滴，威望和奖励券互换"
                        ) {

                        }

                        FunctionItem(
                            icon = R.drawable.ic_integral,
                            name = "积分记录"
                        ) {

                        }
                    }
                }
            }
        }

    }

}

@Composable
private fun FunctionItem(
    icon: Int,
    name: String?,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    onClick: () -> Unit
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50)
            )
            .clip(
                shape = RoundedCornerShape(50)
            )
            .clickable(unbound = false) {
                onClick.invoke()
            }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = name.toString(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        )
    }
}