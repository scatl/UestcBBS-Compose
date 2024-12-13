package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig

/**
 * Created by sca_tl at 2024/7/12 14:17:50
 */
@Composable
fun NinePicGridLayout(
    images: List<ImageViewerConfig.ImageViewerItem>,
    modifier: Modifier = Modifier,
    gridSpace: Dp = 5.dp,
    imageCorner: Dp = 5.dp,
    onClick: (index: Int, images: List<ImageViewerConfig.ImageViewerItem>) -> Unit = { _, _ -> }
) {
    BoxWithConstraints(modifier = modifier) {
        val totalWidth = maxWidth

        when (images.size) {
            1 -> {
                AsyncImage(
                    model = images[0].thumbUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(totalWidth / 3)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(imageCorner))
                        .clickable {
                            onClick(0, images)
                        },
                    contentScale = ContentScale.Crop
                )
            }
            2 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                    images.forEachIndexed { index, entity ->
                        AsyncImage(
                            model = entity.thumbUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width((totalWidth - gridSpace) / 2)
                                .aspectRatio(1f)
                                .clip(shape = RoundedCornerShape(imageCorner))
                                .clickable {
                                    onClick(index, images)
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            3 -> {
                val leftImgH = ((totalWidth - gridSpace) / 2) * 1.5f
                val leftImgW = (totalWidth - gridSpace) / 2
                Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                    AsyncImage(
                        model = images[0].thumbUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(leftImgW)
                            .height(leftImgH)
                            .clip(shape = RoundedCornerShape(imageCorner))
                            .clickable {
                                onClick(0, images)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(gridSpace)) {
                        for (index in 1..2) {
                            AsyncImage(
                                model = images[index].thumbUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(leftImgW)
                                    .height((leftImgH - gridSpace) / 2)
                                    .clip(shape = RoundedCornerShape(imageCorner))
                                    .clickable {
                                        onClick(index, images)
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            4 -> {
                val imgW = (totalWidth - gridSpace) / 2
                Column(verticalArrangement = Arrangement.spacedBy(gridSpace)) {
                    for (row in 0 until 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                            for (col in 0 until 2) {
                                val index = row * 2 + col
                                if (index < images.size) {
                                    AsyncImage(
                                        model = images[index].thumbUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(imgW)
                                            .aspectRatio(1f)
                                            .clip(shape = RoundedCornerShape(imageCorner))
                                            .clickable {
                                                onClick(index, images)
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
            5 -> {
                val ltImgW = (totalWidth - gridSpace) / 3
                val rtImgW = (totalWidth - gridSpace) / 3 * 2
                val bottomRImgW = (totalWidth - gridSpace * 2) / 3
                Column(verticalArrangement = Arrangement.spacedBy(gridSpace)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                        AsyncImage(
                            model = images[0].thumbUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(ltImgW)
                                .height(ltImgW * 1.5f)
                                .clip(shape = RoundedCornerShape(imageCorner))
                                .clickable {
                                    onClick(0, images)
                                },
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = images[1].thumbUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(rtImgW)
                                .height(ltImgW * 1.5f)
                                .clip(shape = RoundedCornerShape(imageCorner))
                                .clickable {
                                    onClick(1, images)
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                        AsyncImage(
                            model = images[2].thumbUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(ltImgW)
                                .aspectRatio(1f)
                                .clip(shape = RoundedCornerShape(imageCorner))
                                .clickable {
                                    onClick(2, images)
                                },
                            contentScale = ContentScale.Crop
                        )
                        for (index in 3 until 5) {
                            AsyncImage(
                                model = images[index].thumbUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(bottomRImgW)
                                    .height(ltImgW)
                                    .clip(shape = RoundedCornerShape(imageCorner))
                                    .clickable {
                                        onClick(index, images)
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            else -> {
                val imgW = (totalWidth - gridSpace * 2) / 3
                Column(verticalArrangement = Arrangement.spacedBy(gridSpace)) {
                    for (row in 0 until 3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(gridSpace)) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                if (index < images.size) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = images[index].thumbUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .width(imgW)
                                                .aspectRatio(1f)
                                                .clip(shape = RoundedCornerShape(imageCorner))
                                                .clickable {
                                                    onClick(index, images)
                                                },
                                            contentScale = ContentScale.Crop
                                        )
                                        if (index == 8 && images.size > 9) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .width(imgW)
                                                    .align(Alignment.Center)
                                                    .aspectRatio(1f)
                                                    .clip(shape = RoundedCornerShape(imageCorner))
                                                    .background(color = Color.Gray.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    "+${images.size - 9}",
                                                    color = Color.White,
                                                    fontSize = 24.sp,
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}