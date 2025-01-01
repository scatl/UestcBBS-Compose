package com.scatl.markdown.plugins

import androidx.compose.ui.graphics.toArgb
import com.scatl.markdown.MarkdownTheme
import com.scatl.markdown.factory.BlockQuoteSpanFactory
import com.scatl.markdown.factory.CodeSpanFactory
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.BlockQuote
import org.commonmark.node.Code

class MarkdownCorePlugin(
    private val theme: MarkdownTheme,
    private val onTextAddedListeners: MutableList<OnTextAddedListener> = ArrayList(0)
) : CorePlugin() {

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        super.configureVisitor(builder)
    }

    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.linkColor(theme.linkColor.toArgb())
        builder.blockQuoteColor(theme.blockQuoteColor.toArgb())
        builder.blockQuoteWidth(theme.blockQuoteWidth)
        builder.codeBackgroundColor(theme.codeBgColor.toArgb())
        builder.thematicBreakColor(theme.thematicBreakColor.toArgb())
        builder.thematicBreakHeight(theme.thematicBreakHeight)
        builder.headingBreakColor(theme.thematicBreakColor.toArgb())
        builder.headingBreakHeight(theme.thematicBreakHeight)
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        super.configureSpansFactory(builder)
        builder.setFactory(BlockQuote::class.java, BlockQuoteSpanFactory(theme))
        builder.setFactory(Code::class.java, CodeSpanFactory(theme))
    }

    override fun addOnTextAddedListener(onTextAddedListener: OnTextAddedListener): CorePlugin {
        onTextAddedListeners.add(onTextAddedListener)
        return this
    }
}