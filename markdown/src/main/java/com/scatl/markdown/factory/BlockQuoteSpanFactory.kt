package com.scatl.markdown.factory

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import androidx.compose.ui.graphics.toArgb
import com.scatl.markdown.MarkdownTheme
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory
import io.noties.markwon.core.MarkwonTheme
import kotlin.math.max
import kotlin.math.min

/**
 * Created by sca_tl at 2024/12/31 11:14:15
 */
class BlockQuoteSpanFactory(private val theme: MarkdownTheme): SpanFactory {
    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {
        return BlockQuoteSpan(configuration.theme(), theme)
    }
}

class BlockQuoteSpan(
    val theme: MarkwonTheme,
    val customTheme: MarkdownTheme
): LeadingMarginSpan, ForegroundColorSpan(customTheme.blockQuoteTextColor.toArgb()) {

    private val rect = Rect()
    private val paint = Paint()

    override fun getLeadingMargin(first: Boolean): Int {
        return customTheme.blockQuoteMargin
    }

    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
    }

    override fun getForegroundColor(): Int {
        return super.getForegroundColor()
    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint?,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        val width = theme.blockQuoteWidth
        paint.set(p)
        theme.applyBlockQuoteStyle(paint)

        val l = x + (dir * width)
        val r = l + (dir * width)
        val left = min(l.toDouble(), r.toDouble()).toInt()
        val right = max(l.toDouble(), r.toDouble()).toInt()

        rect.set(left, top, right, bottom)

        c.drawRect(rect, paint)
    }

}