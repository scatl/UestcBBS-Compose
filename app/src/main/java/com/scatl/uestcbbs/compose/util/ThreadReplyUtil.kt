package com.scatl.uestcbbs.compose.util

object ThreadReplyUtil {

    fun parseBBCode(input: String): List<ThreadReplyItem> {
        // 用于存储解析结果
        val items = mutableListOf<ThreadReplyItem>()

        // 正则表达式匹配对应的标签
        val tagRegex = "(?i)\\[quote](.*?)\\[/quote]|\\[url=(.*?)](.*?)\\[/url]|\\[attachment=(.*?)\\b.*?\\[/attachment]|\\[(.*?)].*?\\[/\\5]".toRegex(RegexOption.DOT_MATCHES_ALL)

        // 用于找到所有标签并生成对应的实体类
        var searchPosition = 0

        tagRegex.findAll(input).forEach { matchResult ->
            // 处理标签前的普通文本
            if (matchResult.range.first > searchPosition) {
                val text = input.substring(searchPosition, matchResult.range.first)
                if (text.isNotBlank()) {
                    items.add(ThreadReplyItem.Text(text.trim()))
                }
            }

            // 处理标签内容
            val quoteText = matchResult.groups[1]?.value
            val url = matchResult.groups[2]?.value
            val urlText = matchResult.groups[3]?.value
            val attachment = matchResult.groups[4]?.value
            // val other = matchResult.groups[5]?.value  // 无需处理，替换为空字符串

            when {
                !quoteText.isNullOrBlank() -> {
                    val innerItems = parseBBCode(quoteText)
                    items.add(ThreadReplyItem.Quote(innerItems))
                }
                !url.isNullOrBlank() && !urlText.isNullOrBlank() -> {
                    items.add(ThreadReplyItem.Url(url.trim(), urlText.trim()))
                }
                !attachment.isNullOrBlank() -> {
                    items.add(ThreadReplyItem.Attachment(attachment.trim()))
                }
                // 无需处理 "other"，它会被转换为空字符串，即跳过
            }

            // 更新搜索位置
            searchPosition = matchResult.range.last + 1
        }

        // 处理最后一段普通文本
        if (searchPosition < input.length) {
            val text = input.substring(searchPosition)
            if (text.isNotBlank()) {
                items.add(ThreadReplyItem.Text(text.trim()))
            }
        }

        return items
    }
}

sealed class ThreadReplyItem {
    data class Text(
        var text: String,
    ): ThreadReplyItem()

    data class Quote(
        var items: List<ThreadReplyItem>,
    ): ThreadReplyItem()

    data class Url(
        var url: String,
        var text: String
    ): ThreadReplyItem()

    data class Attachment(
        var attachmentInfo: String
    ): ThreadReplyItem()
}