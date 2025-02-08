package com.scatl.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Created by sca_tl at 2024/12/31 10:27:20
 */
object MarkdownDefaults {

    @Composable
    fun defaultTheme(
        textColor: Color = MaterialTheme.colorScheme.onBackground,
        linkColor: Color = MaterialTheme.colorScheme.primary,
        blockQuoteColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        blockQuoteTextColor: Color = MaterialTheme.colorScheme.outline,
        blockQuoteWidth: Int = with(LocalDensity.current) { 2.dp.toPx().toInt() },
        blockQuoteMargin: Int = with(LocalDensity.current) { 8.dp.toPx().toInt() },
        codeBgColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        inlineCodeTextColor: Color = MaterialTheme.colorScheme.primary,
        taskListCheckedColor: Color = MaterialTheme.colorScheme.primary,
        taskListUnCheckOutlineColor: Color = MaterialTheme.colorScheme.primary,
        tableHeadBgColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tableOddRowBgColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
        thematicBreakColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        thematicBreakHeight: Int = with(LocalDensity.current) { 2.dp.toPx().toInt() },
    ): MarkdownTheme =
        MarkdownTheme(
            linkColor = linkColor,
            textColor = textColor,
            blockQuoteColor = blockQuoteColor,
            blockQuoteWidth = blockQuoteWidth,
            blockQuoteTextColor = blockQuoteTextColor,
            blockQuoteMargin = blockQuoteMargin,
            codeBgColor = codeBgColor,
            inlineCodeTextColor = inlineCodeTextColor,
            taskListCheckedColor = taskListCheckedColor,
            taskListUnCheckOutlineColor = taskListUnCheckOutlineColor,
            tableHeadBgColor = tableHeadBgColor,
            tableOddRowBgColor = tableOddRowBgColor,
            thematicBreakColor = thematicBreakColor,
            thematicBreakHeight = thematicBreakHeight
        )
}

@Immutable
class MarkdownTheme(
    val linkColor: Color = Color.Unspecified,
    val textColor: Color = Color.Unspecified,
    val blockQuoteColor: Color = Color.Unspecified,
    val blockQuoteTextColor: Color = Color.Unspecified,
    val blockQuoteWidth: Int = 0,
    val blockQuoteMargin: Int = 0,
    val codeBgColor: Color = Color.Unspecified,
    val inlineCodeTextColor: Color = Color.Unspecified,
    val taskListCheckedColor: Color = Color.Unspecified,
    val taskListUnCheckOutlineColor: Color = Color.Unspecified,
    val tableHeadBgColor: Color = Color.Unspecified,
    val tableOddRowBgColor: Color = Color.Unspecified,
    val thematicBreakColor: Color = Color.Unspecified,
    val thematicBreakHeight: Int = 0
)