package com.scatl.markdown.factory

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import androidx.compose.ui.graphics.toArgb
import com.scatl.markdown.MarkdownTheme
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory

/**
 * Created by tanlei02 at 2024/12/31 13:47:18
 */
class CodeSpanFactory(val theme: MarkdownTheme): SpanFactory {
    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {
        return CodeSpan(theme)
    }
}

class CodeSpan(
    val theme: MarkdownTheme
): MetricAffectingSpan() {

    override fun updateMeasureState(p: TextPaint) {
        apply(p)
    }

    override fun updateDrawState(ds: TextPaint) {
        apply(ds)
        ds.bgColor = theme.codeBgColor.toArgb()
    }

    private fun apply(p: TextPaint) {
        p.color = theme.inlineCodeTextColor.toArgb()
        p.typeface = Typeface.DEFAULT_BOLD
    }

}