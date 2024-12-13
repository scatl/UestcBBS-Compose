package com.scatl.uestcbbs.compose.widget

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Created by sca_tl at 2024/7/15 20:10:45
 */
class ArcShape(private val curveDepth: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val depthPx = with(density) { curveDepth.toPx() }

        val path = Path().apply {
            moveTo(0f, 0f) // Move to top-left corner
            lineTo(0f, size.height) // Line to bottom-left corner
            quadraticTo(
                size.width / 2, size.height - 2 * depthPx, // Control point
                size.width, size.height // End point at bottom-right corner
            )
            lineTo(size.width, 0f) // Line to top-right corner
            close() // Close the path
        }
        return Outline.Generic(path)
    }
}