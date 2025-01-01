package com.scatl.markdown

import android.content.Context
import android.graphics.Color
import android.text.Spanned
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import coil.imageLoader
import com.scatl.markdown.plugins.MarkdownCorePlugin
import com.scatl.markdown.factory.TaskListDrawable
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin

internal object MarkdownRender {

    fun create(
        context: Context,
        linkifyMask: Int,
        theme: MarkdownTheme,
        beforeSetMarkdown: ((TextView, Spanned) -> Unit)? = null,
        afterSetMarkdown: ((TextView) -> Unit)? = null,
        onLinkClicked: ((String) -> Unit)? = null,
    ): Markwon {
        return Markwon.builderNoCore(context)
            .usePlugin(MarkdownCorePlugin(theme))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(CoilImagesPlugin.create(context, context.imageLoader))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(LinkifyPlugin.create(linkifyMask))
            .usePlugin(TablePlugin.create(
                TableTheme
                    .buildWithDefaults(context)
                    .tableHeaderRowBackgroundColor(theme.tableHeadBgColor.toArgb())
                    .tableOddRowBackgroundColor(theme.tableOddRowBgColor.toArgb())
                    .build()
            ))
            .usePlugin(TaskListPlugin.create(
                TaskListDrawable(
                    theme.taskListCheckedColor.toArgb(),
                    theme.taskListUnCheckOutlineColor.toArgb(),
                    Color.WHITE
                )
            ))
//            .apply {
////                if (enableSoftBreakAddsNewLine) {
//                    usePlugin(SoftBreakAddsNewLinePlugin.create())
////                }
//            }
            //.usePlugin(SyntaxHighlightPlugin())
            .usePlugin(object : AbstractMarkwonPlugin() {

                override fun beforeSetText(textView: TextView, markdown: Spanned) {
                    beforeSetMarkdown?.invoke(textView, markdown)
                }

                override fun afterSetText(textView: TextView) {
                    afterSetMarkdown?.invoke(textView)
                }

                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    // Setting [MarkwonConfiguration.Builder.linkResolver] overrides
                    // Markwon's default behaviour - see Markwon's [LinkResolverDef]
                    // and how it's used in [MarkwonConfiguration.Builder].
                    // Only use it if the client explicitly wants to handle link clicks.
                    if (onLinkClicked != null) {
                        builder.linkResolver { _, link ->
                            // handle individual clicks on Textview link
                            onLinkClicked.invoke(link)
                        }
                    }
                }
            })
            .build()
    }
}
