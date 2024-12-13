package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Created by sca_tl at 2024/9/27 10:03:09
 */
@Composable
fun ScratchCard(
    modifier: Modifier = Modifier,
    onScratchChange: ((isScratching: Boolean) -> Unit)? = null,
    onScratchStarted: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var linePath by remember { mutableStateOf(Offset.Zero) }
    val path by remember { mutableStateOf(Path()) }
    val isScratching = rememberSaveable { mutableStateOf(false) }
    val scratchStarted = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isScratching) {
        snapshotFlow {isScratching.value}.collect {onScratchChange?.invoke(isScratching.value) }
    }

    Column(
        modifier = modifier
            .pointerInput("dragging") {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            //按住时，更新起始点
                            PointerEventType.Press -> {
                                path.moveTo(
                                    event.changes.first().position.x,
                                    event.changes.first().position.y
                                )
                            }
                            //移动时，更新起始点，记录路径path
                            PointerEventType.Move -> {
                                isScratching.value = true
                                if (scratchStarted.value.not()) {
                                    onScratchStarted?.invoke()
                                }
                                scratchStarted.value = true
                                linePath = event.changes.first().position
                            }

                            PointerEventType.Release -> {
                                isScratching.value = false
                            }
                        }
                    }
                }
            }
            .scrapeLayer(path, linePath, scratchStarted.value)
    ) {
        content()
    }
}

private fun Modifier.scrapeLayer(
    startPath: Path,
    moveOffset: Offset,
    scratchStarted: Boolean
) = this.then(ScrapeLayer(startPath, moveOffset, scratchStarted))

private class ScrapeLayer(
    private val startPath: Path,
    private val moveOffset: Offset,
    private val scratchStarted: Boolean
) : DrawModifier {

    private val pathPaint = Paint().apply {
        alpha = 0f
        style = PaintingStyle.Stroke
        strokeWidth = 50f
        blendMode = BlendMode.SrcIn
        strokeJoin = StrokeJoin.Round
        strokeCap = StrokeCap.Round
    }

    private val layerPaint = Paint().apply {
        color = Color.Gray
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        drawIntoCanvas {
            val rect = Rect(0f, 0f, size.width, size.height)
            it.saveLayer(rect, layerPaint)
            it.drawRect(rect, layerPaint)
            if (scratchStarted) {
                startPath.lineTo(moveOffset.x, moveOffset.y)
                it.drawPath(startPath, pathPaint)
            }
            it.restore()
        }
    }
}