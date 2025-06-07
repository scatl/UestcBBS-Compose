package com.scatl.uestcbbs.compose.module.home.newpost.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import com.scatl.uestcbbs.compose.ext.dp2px
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.LoopBanner
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Created by sca_tl at 2024/4/22 20:37:07
 */
@Composable
fun NewThreadBanner(
    data: List<BingDailyPicEntity.Image>,
    modifier: Modifier
) {
    val tag = "HomeBanner"
    val navHostController = LocalNavController.current
    val pagerState = rememberPagerState(pageCount = { data.size + 1 })

    Box(modifier = modifier.height(450.dp)) {
        LoopBanner(
            originDataSize = data.size,
            pagerState = pagerState,
            vertical = false,
            modifier = Modifier
        ) { dataIndex, pageIndex ->
            Box(
                Modifier
                    .graphicsLayer {
                        val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                        translationX = pageOffset * size.width
                        alpha = 1 - pageOffset.absoluteValue
                    }
                    .fillMaxSize()
                    .clickable {
                        val imageViewerItems = mutableListOf<ImageViewerConfig.ImageViewerItem>()
                        data.forEach {
                            imageViewerItems.add(
                                ImageViewerConfig.ImageViewerItem(
                                    originUrl = it.fullOriginUrl,
                                    thumbUrl = it.fullThumbUrl
                                )
                            )
                        }

                        navHostController.navigate(Router.ImageViewerRouterEntity(
                            config = ImageViewerConfig.toJson(
                                ImageViewerConfig(
                                    images = imageViewerItems,
                                    initialIndex = dataIndex
                                )
                            )
                        ))
                    }
            ) {
                AsyncImage(
                    model = data[dataIndex].fullThumbUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = mutableListOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            startY = 80.dp.dp2px,
                            endY = 0f
                        )
                    )
                )

                AnimatedVisibility(
                    pagerState.currentPage == pageIndex,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 10.dp, bottom = 30.dp)
                ) {
                    Text(
                        text = data[dataIndex].copyright?:"",
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .height(24.dp)
                .padding(start = 4.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Start
        ) {
            repeat(data.size) { iteration ->
                val page = if (pagerState.currentPage == data.size) 0 else pagerState.currentPage
                val color: Color
                val offset = (page - iteration) + pagerState.currentPageOffsetFraction
                val lineWeight = if (offset == 0f) {
                    color = Color.White
                    1.5f
                } else if (offset <= -1f || offset >= 1f) {
                    color = Color.White.copy(alpha = 0.5f)
                    1f
                } else {
                    color = Color.White.copy(alpha = 1 - offset / 2 * sign(offset))
                    1.5 - (offset / 2) * sign(offset)
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                        .weight(lineWeight.toFloat())
                        .height(4.dp)
                )
            }
        }
    }

}