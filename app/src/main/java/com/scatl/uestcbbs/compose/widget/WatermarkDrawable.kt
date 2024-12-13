package com.scatl.uestcbbs.compose.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.IntRange
import kotlin.math.sqrt

/**
 * Created by sca_tl at 2024/7/14 20:38
 */
class WatermarkDrawable : Drawable() {
    private val mPaint = Paint()
    var mText: String = ""
    var mTextColor: Int = 0
    var mTextSize: Float = 0f
    var mRotation: Float = 0f

    override fun draw(canvas: Canvas) {
        val width = bounds.right
        val height = bounds.bottom
        val diagonal = sqrt((width * width + height * height).toDouble()).toInt() // 对角线的长度

        mPaint.color = mTextColor
        mPaint.textSize = mTextSize
        mPaint.isFakeBoldText = true
        mPaint.isAntiAlias = true
        val textWidth = mPaint.measureText(mText)

        canvas.drawColor(0x00000000)
        canvas.rotate(mRotation)

        var index = 0
        var fromX: Float
        var positionY = diagonal / 20
        while (positionY <= diagonal) {
            fromX = -width + (index++ % 2) * textWidth
            var positionX = fromX
            while (positionX < width) {
                canvas.drawText(mText, positionX, positionY.toFloat(), mPaint)
                positionX += (textWidth * 1.2).toFloat()
            }
            positionY += diagonal / 10
        }

        canvas.save()
        canvas.restore()
    }

    override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}