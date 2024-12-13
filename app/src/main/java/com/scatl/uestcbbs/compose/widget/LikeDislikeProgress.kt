package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.ext.unboundClickable

/**
 * Created by sca_tl at 2024/8/12 13:36:51
 */
@Composable
fun LikeDislikeProgressBar(
    leftNum: Int,
    rightNum: Int,
    leftIcon: ImageVector = Icons.Default.ThumbDownOffAlt,
    rightIcon: ImageVector = Icons.Default.ThumbUpOffAlt,
    iconSize: Dp,
    progress: Float,
    gapWidth: Float = 10f,
    colors: List<Color>,
    modifier: Modifier,
    onLeftClick: (() -> Unit) ? = null,
    onRightClick: (() -> Unit) ? = null,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val (leftCorrectNum, rightCorrectNum) = when {
                leftNum == 0 && rightNum == 0 -> Pair(1, 1)
                leftNum == 0 && rightNum != 0 -> Pair(1, 3)
                leftNum != 0 && rightNum == 0 -> Pair(3, 1)
                leftNum.toFloat() / rightNum.toFloat() < 0.25f -> Pair(1, 3)
                leftNum.toFloat() / rightNum.toFloat() > 4 -> Pair(3, 1)
                else -> Pair(leftNum, rightNum)
            }

            val offset = when {
                rightCorrectNum == 0 -> h * progress
                leftCorrectNum == 0 -> -h * progress - (h - gapWidth) * progress
                else -> -(h - gapWidth) / 2 * progress
            }

            val leftLength = (leftCorrectNum.toFloat() / (leftCorrectNum + rightCorrectNum).toFloat()) * w * progress - offset
            val rightLength = (rightCorrectNum.toFloat() / (leftCorrectNum + rightCorrectNum).toFloat()) * w * progress - offset

            val clipPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, w, h),
                        cornerRadius = CornerRadius(h / 2)
                    )
                )
            }

            clipPath(clipPath) {
                val leftPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(leftLength, 0f)
                    lineTo(leftLength - h, h)
                    lineTo(0f, h)
                    close()
                }

                val leftGradient = Brush.linearGradient(
                    colors = listOf(
                        colors.getOrElse(0) { Color.Transparent },
                        colors.getOrElse(1) { Color.Transparent }
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(leftLength, h)
                )

                drawPath(
                    path = leftPath,
                    brush = leftGradient
                )

                val rightPath = Path().apply {
                    moveTo(w, 0f)
                    lineTo(w - rightLength + h, 0f)
                    lineTo(w - rightLength, h)
                    lineTo(w, h)
                    close()
                }

                val rightGradient = Brush.linearGradient(
                    colors = listOf(
                        colors.getOrElse(2) { Color.Transparent },
                        colors.getOrElse(3) { Color.Transparent }
                    ),
                    start = Offset(w - rightLength, 0f),
                    end = Offset(w, h)
                )

                drawPath(
                    path = rightPath,
                    brush = rightGradient
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        ) {
            Icon(
                imageVector = leftIcon,
                contentDescription = "left",
                tint = colors.getOrElse(0) { Color.White },
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(50)
                    )
                    .unboundClickable {
                        onLeftClick?.invoke()
                    }
                    .padding(5.dp)
            )
            Text(
                text = leftNum.toString(),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        ) {
            Text(
                text = rightNum.toString(),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )

            Icon(
                imageVector = rightIcon,
                contentDescription = "right",
                tint = colors.getOrElse(3) { Color.White },
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(50)
                    )
                    .unboundClickable {
                        onRightClick?.invoke()
                    }
                    .padding(5.dp)
            )
        }
    }
}

@Preview
@Composable
fun LikeDislikeProgressBarTest() {
    val leftNum = 100
    val rightNum = 1
    val progress = 1f
    val gapWidth = 25f

    val leftStartColor = Color.Blue
    val leftEndColor = Color.LightGray
    val rightStartColor = Color.Green
    val rightEndColor = Color.LightGray

    LikeDislikeProgressBar(
        leftNum,
        rightNum,
        progress = progress,
        gapWidth = gapWidth,
        colors = listOf(leftStartColor, leftEndColor, rightStartColor, rightEndColor),
        iconSize = 30.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 0.dp)
    )
}
