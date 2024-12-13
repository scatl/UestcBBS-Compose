package com.scatl.markdown

import android.content.Context
import android.text.Spanned
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil.ImageLoader
import coil.imageLoader
import com.scatl.markdown.plugins.CoilImagePlugin
import com.scatl.markdown.plugins.core.MarkdownCorePlugin
import com.scatl.markdown.plugins.syntaxhighlight.SyntaxHighlightPlugin
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin

internal object MarkdownRender {

    fun create(
        context: Context,
        imageLoader: ImageLoader?,
        linkifyMask: Int,
        enableSoftBreakAddsNewLine: Boolean,
        syntaxHighlightColor: Color,
        syntaxHighlightTextColor: Color,
        headingBreakColor: Color,
        quoteBorderColor: Color,
        quoteTextColor: Color,
        enableUnderlineForLink: Boolean,
        beforeSetMarkdown: ((TextView, Spanned) -> Unit)? = null,
        afterSetMarkdown: ((TextView) -> Unit)? = null,
        onLinkClicked: ((String) -> Unit)? = null,
    ): Markwon {
        val coilImageLoader = imageLoader ?: context.imageLoader
        return Markwon.builderNoCore(context)
            .usePlugin(
                MarkdownCorePlugin(
                    syntaxHighlightColor = syntaxHighlightColor.toArgb(),
                    syntaxHighlightTextColor = syntaxHighlightTextColor.toArgb(),
                    enableUnderlineForLink = enableUnderlineForLink,
                    quoteBorderColor = quoteBorderColor.toArgb(),
                    quoteTextColor = quoteTextColor.toArgb()
                )
            )
            .usePlugin(HtmlPlugin.create())
            //.usePlugin(CoilImagesPlugin.create(context, coilImageLoader))
            .usePlugin(CoilImagePlugin.create(context, coilImageLoader))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(LinkifyPlugin.create(linkifyMask))
            .usePlugin(TaskListPlugin.create(context))
            .apply {
                if (enableSoftBreakAddsNewLine) {
                    usePlugin(SoftBreakAddsNewLinePlugin.create())
                }
            }
            .usePlugin(SyntaxHighlightPlugin())
            .usePlugin(object : AbstractMarkwonPlugin() {

                override fun beforeSetText(textView: TextView, markdown: Spanned) {
                    beforeSetMarkdown?.invoke(textView, markdown)
                }

                override fun afterSetText(textView: TextView) {
                    afterSetMarkdown?.invoke(textView)
                }

                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    if (headingBreakColor == Color.Transparent) {
                        builder.headingBreakColor(1)
                    } else {
                        builder.headingBreakColor(headingBreakColor.toArgb())
                    }
                }

                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    // Setting [MarkwonConfiguration.Builder.linkResolver] overrides
                    // Markwon's default behaviour - see Markwon's [LinkResolverDef]
                    // and how it's used in [MarkwonConfiguration.Builder].
                    // Only use it if the client explicitly wants to handle link clicks.
                    onLinkClicked ?: return
                    builder.linkResolver { _, link ->
                        // handle individual clicks on Textview link
                        onLinkClicked.invoke(link)
                    }
                }
            })
            .build()
    }
}
