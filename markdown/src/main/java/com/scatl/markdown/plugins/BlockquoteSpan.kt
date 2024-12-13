package com.scatl.markdown.plugins

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import io.noties.markwon.core.MarkwonTheme
import kotlin.math.max
import kotlin.math.min

/**
 * Created by tanlei02 at 2024/12/9 17:07:17
 */
class BlockquoteSpan (
    val theme: MarkwonTheme,
    val borderColor: Int,
    val quoteTextColor: Int
) : LeadingMarginSpan, ForegroundColorSpan(quoteTextColor) {

    private val rect = Rect()

    override fun getLeadingMargin(first: Boolean) = theme.blockQuoteWidth * 3

//    override fun updateDrawState(textPaint: TextPaint) {
//        textPaint.color = quoteTextColor
//    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        val style = p.style
        val color = p.color

        val width = theme.blockQuoteWidth
        p.style = Paint.Style.FILL
        p.setColor(borderColor)

        val l = x + (dir * width)
        val r = l + (dir * width)
        val left = min(l.toDouble(), r.toDouble()).toInt()
        val right = max(l.toDouble(), r.toDouble()).toInt()
        rect.set(5, top, 5 + width, bottom)
        c.drawRect(rect, p)

        p.style = style
        p.color = color
    }
}