package com.scatl.markdown.plugins.core

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.scatl.markdown.plugins.BlockquoteSpan
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.SimpleBlockNodeVisitor
import io.noties.markwon.core.factory.CodeBlockSpanFactory
import io.noties.markwon.core.factory.CodeSpanFactory
import io.noties.markwon.core.factory.EmphasisSpanFactory
import io.noties.markwon.core.factory.HeadingSpanFactory
import io.noties.markwon.core.factory.LinkSpanFactory
import io.noties.markwon.core.factory.ListItemSpanFactory
import io.noties.markwon.core.factory.StrongEmphasisSpanFactory
import io.noties.markwon.core.factory.ThematicBreakSpanFactory
import io.noties.markwon.image.ImageProps
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListBlock
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak

class MarkdownCorePlugin(
    val syntaxHighlightColor: Int,
    val syntaxHighlightTextColor: Int,
    val quoteBorderColor: Int,
    val quoteTextColor: Int,
    val enableUnderlineForLink: Boolean = true,
    val onTextAddedListeners: MutableList<OnTextAddedListener> = ArrayList(0)
) : CorePlugin() {

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        super.configureVisitor(builder)
//        text(builder)
//        strongEmphasis(builder)
//        emphasis(builder)
//        blockQuote(builder)
//        code(builder)
//        fencedCodeBlock(builder)
//        indentedCodeBlock(builder)
//        image(builder)
//        bulletList(builder)
//        orderedList(builder)
//        listItem(builder)
//        thematicBreak(builder)
//        heading(builder)
//        softLineBreak(builder)
//        hardLineBreak(builder)
//        paragraph(builder)
//        link(builder)
    }

    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.codeBackgroundColor(syntaxHighlightColor)
        builder.isLinkUnderlined(enableUnderlineForLink)
        if (syntaxHighlightTextColor != Color.Unspecified.toArgb()) {
            builder.codeTextColor(syntaxHighlightTextColor)
        }
        super.configureTheme(builder)
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        super.configureSpansFactory(builder)
        builder.setFactory(BlockQuote::class.java) { configuration, props ->
            BlockquoteSpan(
                theme = configuration.theme(),
                borderColor = quoteBorderColor,
                quoteTextColor = quoteTextColor
            )
        }
    }

    override fun addOnTextAddedListener(onTextAddedListener: OnTextAddedListener): CorePlugin {
        onTextAddedListeners.add(onTextAddedListener)
        return this
    }

}