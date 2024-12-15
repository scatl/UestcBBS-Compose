package com.scatl.uestcbbs.compose.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.AttachmentEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.toBBSImgUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.EmotionManager
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig

/**
 * Created by sca_tl at 2024/7/20 13:31
 */
object HtmlUtil {

    @JvmStatic
    fun generateStyledHtmlContent(
        content: String,
        defaultFontSize: Int,
        backgroundColor: String,
        textColor: String,
        linkColor: String,
        contentBorderColor: String,
        contentBgColor: String,
        quoteBgColor: String,
        codeBgColor: String
    ): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-size: ${defaultFontSize}px;
                        margin: 0;
                        padding: 0;
                        line-height: 1.7;
                        background-color: $backgroundColor;
                        color: $textColor;
                        word-break: break-all;
                        overflow-wrap: break-word;
                    }          
                    blockquote {
                        border-left: 4px solid ${contentBorderColor};
                        margin: 1.5em 0px;
                        padding: 0.5em 10px;
                        background: ${quoteBgColor};
                    }
                    a {
                        color: $linkColor;
                        text-decoration: underline;
                    }
                    a:hover {
                        text-decoration: underline;
                    }                   
                    .attachimg {
                        width: calc(100% - 2px);
                        height: auto;
                        border: 1px solid ${contentBorderColor};
                        border-radius: 8px;
                        box-sizing: border-box;
                        display:block;
                        margin: 10px auto;
                    }
                    .attachaudio {
                        width: calc(100% - 22px);
                        height: auto;
                        border: 1px solid ${contentBorderColor};
                        border-radius: 8px;
                        padding: 10px;
                    }
                    .img {
                        width: calc(100% - 2px);
                        height: auto;
                        border-radius: 8px;
                        box-sizing: border-box;
                        display:block;
                        margin: 10px auto;
                    }
                    .img-inline {
                        width: auto;
                        height: auto;                                               
                        display: inline-block; 
                        margin: 10px 0;
                        vertical-align: middle;
                    }
                    .attachment {
                        width: calc(100% - 2px);
                        background-color: ${contentBgColor};
                        padding: 10px;
                        border: 1px solid ${contentBorderColor};
                        border-radius: 8px;
                        box-sizing: border-box;
                        margin: 10px auto;
                    }
                    .emoji {
                        vertical-align: middle;
                        height: 25px;
                    }
                    .table_split {
                        border: 1px solid ${contentBorderColor}; 
                        padding: 8px
                    }
                    .video {
                        border: 1px solid ${contentBorderColor};
                        border-radius: 8px;
                        width: calc(100% - 2px); 
                        height: 200px;
                        box-sizing: border-box;
                    }
                    pre code {
                        display: block;
                        width: calc(100% - 22px);
                        padding: 10px;
                        background-color: ${codeBgColor};
                        border: 1px solid ${contentBorderColor};
                        border-radius: 4px;
                        overflow-x: auto;
                    }
                    code {
                        background-color: ${codeBgColor};
                        padding: 2px 4px;                       
                        border-radius: 4px;
                    }
                    span[color], p[color], div[color] {
                        color: inherit !important;
                    }
                </style>
            </head>
            <body>
                $content
                
                <script type="text/javascript">
                    function resizeImage(img) {
                        var id = img.getAttribute('id');
                        if (img.complete) {
                            var ratio = img.naturalHeight / img.naturalWidth;
                            var newHeight = img.clientWidth * ratio + "px";
                            img.style.height = newHeight;
                            localStorage.setItem(id + "-height", newHeight);
                        } else {
                            img.onload = function() {
                                var ratio = img.naturalHeight / img.naturalWidth;
                                var newHeight = img.clientWidth * ratio + "px";
                                img.style.height = newHeight;
                                localStorage.setItem(id + "-height", newHeight);
                            };
                        }
                    }
            
                    function applyStoredHeight(img) {
                        var id = img.getAttribute('id');
                        var storedHeight = localStorage.getItem(id + "-height");
                        if (storedHeight) {
                            img.style.height = storedHeight;
                        } else {
                            resizeImage(img);
                        }
                    }
            
                    function resizeAllImages() {
                        var images = document.querySelectorAll('.attachimg, .img');
                        images.forEach(function(img, index) {
                            var id = img.getAttribute('src');
                            img.setAttribute('id', id);
                            applyStoredHeight(img);
                        });
                    }
            
                    document.addEventListener("DOMContentLoaded", function() {
                        resizeAllImages();
                    });
                
                    function onImageClick(url) {
                        Android.onImageClick(url);
                    }
                    
                    function onAttachmentClick(url, name) {
                        Android.onAttachmentClick(url, name);
                    }
                </script>
            </body>
            </html>
            """
    }

    @JvmStatic
    fun bbcodeToHtml(
        context: Context,
        input: String?,
        attachments: List<AttachmentEntity>? = null,
        imageViewerConfig: ImageViewerConfig,
        defaultFontSize: Int,
        textColor: String,
        bgColor: String
    ): String {
        if (input == null) {
            return ""
        }

        val inputIncludedAttachments = mutableListOf<AttachmentEntity>()

        val regexReplacements = listOf<Pair<Regex, (match: MatchResult) -> String>>(

            Pair("\\[b](.*?)\\[/b]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<strong>${match.groupValues[1]}</strong>"
            },

            Pair("\\[p(?:=(.*?))?](.*?)\\[/p]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val params = match.groupValues[1]
                if (params.isEmpty()) {
                    "<div>${match.groupValues[2]}</div>"
                } else {
                    val align = if (params.contains("right")) {
                        "right"
                    } else if (params.contains("center")) {
                        "center"
                    } else {
                        "left"
                    }
                    "<div style=\"text-align:${align}\">${match.groupValues[2]}</div>"
                }
            },

            Pair("\\[i=s](.*?)\\[/i]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<em style='color:gray; font-size:smaller;'>${match.groupValues[1]}</em>"
            },

            Pair("\\[i](.*?)\\[/i]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<em>${match.groupValues[1]}</em>"
            },

            Pair("\\[u](.*?)\\[/u]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<u>${match.groupValues[1]}</u>"
            },

            Pair("\\[center](.*?)\\[/center]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<div style=\"text-align:center;\">${match.groupValues[1]}</div>"
            },

            Pair("\\[li](.*?)\\[/li]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<li>${match.groupValues[1]}</li>"
            },

            Pair("\\[align=left](.*?)\\[/align]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<div style=\"text-align:left;\">${match.groupValues[1]}</div>"
            },

            Pair("\\[align=right](.*?)\\[/align]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<div style=\"text-align:right;\">${match.groupValues[1]}</div>"
            },

            Pair("\\[align=center](.*?)\\[/align]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<div style=\"text-align:center;\">${match.groupValues[1]}</div>"
            },

            Pair("\\[quote](.*?)\\[/quote]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<blockquote>${match.groupValues[1]}</blockquote>"
            },

            Pair("\\[code](.*?)\\[/code]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<pre><code>${match.groupValues[1].trimStart().trimEnd()}</code></pre>"
            },

            Pair("\\[backcolor=(.*?)](.*?)\\[/backcolor]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<span style=\"background-color:${match.groupValues[1]};\">${match.groupValues[2]}</span>"
            },

            Pair("\\[td(?:=(\\d+)(?:,(\\d+))?(?:,(\\d+))?)?](.*?)\\[/td]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val param1 = match.groups[1]?.value
                val param2 = match.groups[2]?.value
                val param3 = match.groups[3]?.value
                val content = match.groupValues[4]

                when {
                    param1 != null && param2 != null && param3 != null ->
                        "<td colspan=\"$param1\" rowspan=\"$param2\" data-custom=\"$param3\" style=\"word-wrap: break-word;\">$content</td>"
                    param1 != null && param2 != null ->
                        "<td colspan=\"$param1\" rowspan=\"$param2\" style=\"word-wrap: break-word;\">$content</td>"
                    param1 != null ->
                        "<td data-custom=\"$param1\" style=\"word-wrap: break-word;\">$content</td>"
                    else ->
                        "<td>$content</td>"
                }
            },

            Pair("\\[tr(?:=([^]]+))?](.*?)\\[/tr](\\r\\n|\\r|\\n)*".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val color = match.groups[1]?.value
                val content = match.groupValues[2]

                val realColor = try {
                    if (ColorUtil.colorDistance(color!!, textColor) < 10) { bgColor } else { color }
                } catch (e: Exception) {
                    color
                }

                if (realColor != null) {
                    "<tr style=\"background-color: $realColor;\">$content</tr>"
                } else {
                    "<tr>$content</tr>"
                }
            },

            Pair("\\[list(?:=(.*?))?](.*?)\\[/list]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val listType = match.groupValues[1]
                val content = match.groupValues[2]

                val items = content.split("\\[\\*]".toRegex()).filter { it.isNotBlank() }
                val listTag = if (listType.isNotEmpty()) "ol" else "ul"

                "<$listTag>${items.joinToString("") { it.trim() }}</$listTag>"
            },

            Pair("\\[img(?:=(.*?))?](.*?)\\[/img](\\r\\n|\\r|\\n)*".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val size = match.groupValues[1].split(",")
                val url = match.groupValues[2]
                imageViewerConfig.images.add(
                    ImageViewerConfig.ImageViewerItem(url, url)
                )

                val width = size.getOrNull(0).toIntOrElse(-1)
                val height = size.getOrNull(1).toIntOrElse(-1)

//                if (width > 0 && height > 0) {
//                    "<img class=\"img-inline\" src=\"${url}\" onclick=\"onImageClick('${url}')\" onload=\"applyStoredHeight(this)\" />"
//                } else {
                    "<img class=\"img\", src=\"${url}\" onclick=\"onImageClick('${url}')\" onload=\"applyStoredHeight(this)\" />"
//                }
            },

            Pair("\\[url(?:=(.*?))?](.*?)\\[/url]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val url = match.groupValues[1]
                val text = match.groupValues[2]
                val href = url.ifEmpty { text }
                "<a href=\"$href\">$text</a>"
            },

            Pair("\\[color=(.*?)](.*?)\\[/color]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val color = match.groupValues[1]
                val content = match.groupValues[2]
                val realColor = try {
                    if (ColorUtil.colorDistance(color, bgColor) < 10) { textColor } else { color }
                } catch (e: Exception) {
                    color
                }
                "<span style=\"color:${realColor}\">$content</span>"
            },

            Pair("\\[table(?:=(.*?))?](.*?)\\[/table]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val attributes = match.groupValues[1].split(",")
                val color = if (attributes.size > 1) attributes[1] else ""
                "<table style=\"width:100%; ${if (color.isNotNullAndEmpty()) "background-color:$color" else ""}\">${match.groupValues[2]}</table>"
            },

            Pair("\\[size=(.*?)](.*?)\\[/size]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val size = match.groupValues[1]
                val content = match.groupValues[2]
                "<span style=\"font-size:${defaultFontSize}px\">$content</span>"
            },

            Pair("\\\\u([0-9A-Fa-f]{4})".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val charCode = match.groupValues[1].toInt(16)
                charCode.toChar().toString()
            },

            Pair("\\[font=(.*?)](.*?)\\[/font]".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val fontFamily = match.groupValues[1]
                val text = match.groupValues[2]
                "<span style=\"font-family:$fontFamily\">$text</span>"
            },

            Pair("(\\[([as]):\\d*])".toRegex()) { match ->
                val emojiId = match.groupValues[1]
                val item = EmotionManager.getEmotionByName(emojiId.replace(":", "_"))
                "<img class=\"emoji\" src=${item?.aPath} />"
            },

            Pair("\\[attach](.*?)\\[/attach](\\r\\n|\\r|\\n)*".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                val id = match.groupValues[1]
                val attachment = attachments?.find { it.attachmentId.toString() == id }
                if (attachment != null) {
                    inputIncludedAttachments.add(attachment)
                }
                getAttachmentHtml(context, attachment, imageViewerConfig)
            },

            Pair("\\s+(http[s]?://\\S+)\\s+".toRegex()) { match ->
                " <a href=\"${match.groupValues[1]}\">${match.groupValues[1]}</a> "
            },

            //at
            Pair("\\[@([^]]+)]\\(at:(\\d+)\\)".toRegex()) { match ->
                val username = match.groupValues[1]
                val userId = match.groupValues[2]
                "<a href=\"${Constants.BBS_URL}/user/$userId\">@$username</a>"
            },

            Pair("!\\[(.*?)]\\((.*?)\\)".toRegex()) { match ->
                val id = match.groupValues[2]
                if (id == "s") {
                    //emoji
                    val emojiId = match.groupValues[1]
                    val item = EmotionManager.getEmotionById(emojiId)
                    "<img class=\"emoji\" src=${item?.aPath} />"
                } else {
                    val attachment = attachments
                        ?.find {
                            it.attachmentId.toString() == id.replace("i:", "")
                        }
                    if (attachment?.isImage == 1) {
                        imageViewerConfig.images.add(
                            ImageViewerConfig.ImageViewerItem(attachment.path, attachment.thumbnailUrl)
                        )
                        "<img class=\"attachimg\", " +
                                "src=\"${attachment.path.toBBSImgUrl()}\" " +
                                "onclick=\"onImageClick('${attachment.thumbnailUrl}'')\"" +
                                "onload=\"applyStoredHeight(this)\"" +
                                "alt=\"${attachment.description}\"/>"
                    } else {
                        ""
                    }
                }
            },
        )

        var result = input
            .replace("[*]", "<li>")
            .replace("[hr]", "<hr/>")
            .replace(" ", "&nbsp;")

        var matchOccurred: Boolean
        do {
            matchOccurred = false
            regexReplacements.forEach { (regex, replacement) ->
                result = regex.replace(result) { matchResult ->
                    matchOccurred = true
                    replacement.invoke(matchResult)
                }
            }
        } while (matchOccurred)

        //部分帖子附件信息不在正文里
        if (attachments.isNotNullAndEmpty()) {
            var appendTitle = true
            attachments.forEach {
                if (it.attachmentId != null && it.attachmentId > 0 && !inputIncludedAttachments.contains(it)) {
                    if (appendTitle) {
                        result += "<br/>${ContextCompat.getString(context, R.string.attachment)}：<br/>"
                        appendTitle = false
                    }
                    result += getAttachmentHtml(context, it, imageViewerConfig)
                }
            }
        }

        result = result
            .trimStart()
            .trimEnd()
            .replace("\r\n", "<br/>")
            .replace("\n", "<br/>")

        return result
    }

    @JvmStatic
    fun markdownToHtml(
        context: Context,
        input: String?,
        attachments: List<AttachmentEntity>? = null,
        imageViewerConfig: ImageViewerConfig
    ): String {
        if (input == null) {
            return ""
        }

//        val flavour = CommonMarkFlavourDescriptor()
//        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(input)
//        val html = HtmlGenerator(input, parsedTree, flavour).generateHtml()

        val regexReplacements = listOf<Pair<Regex, (match: MatchResult) -> String>>(
            Pair("^######\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h6>${match.groupValues[1]}</h6>"
            },

            Pair("^#####\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h5>${match.groupValues[1]}</h5>"
            },

            Pair("^####\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h4>${match.groupValues[1]}</h4>"
            },

            Pair("^###\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h3>${match.groupValues[1]}</h3>"
            },

            Pair("^##\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h2>${match.groupValues[1]}</h2><hr/>"
            },

            Pair("^#\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
                "<h1>${match.groupValues[1]}</h1><hr/>"
            },

            //匹配顺序不能变了，先匹配列表，再匹配粗体和斜体
            Pair("(?:\\r?\\n)?^-\\s+(.*?)\\s*$".toRegex(RegexOption.MULTILINE)) { match ->
                "<li>${match.groupValues[1]}</li>"
            },

            Pair("(?:\\r?\\n)?^\\*\\s+(.*?)\\s*$".toRegex(RegexOption.MULTILINE)) { match ->
                "<li>${match.groupValues[1]}</li>"
            },

            Pair("\\*\\*(.*?)\\*\\*".toRegex(RegexOption.DOT_MATCHES_ALL)) { match ->
                "<strong>${match.groupValues[1]}</strong>"
            },

            Pair("\\*(.*?)\\*".toRegex(RegexOption.MULTILINE)) { match ->
                "<em>${match.groupValues[1]}</em>"
            },

//            Pair("`(.*?)`".toRegex()) { match ->
//                "<code>${match.groupValues[1]}</code>"
//            },

            Pair(Regex("```(.*?)```", RegexOption.DOT_MATCHES_ALL)) { match ->
                "<pre><code>${match.groupValues[1].trimStart().trimEnd()}</code></pre>"
            },

            Pair("`([^`]*)`".toRegex()) { match ->
                "<code>${match.groupValues[1]}</code>"
            },

//            Pair("^>\\s*(.*?)\$".toRegex(RegexOption.MULTILINE)) { match ->
//                "<blockquote>${match.groupValues[1]}</blockquote>"
//            },

            Pair("^>(.*(?:\\n>.*)*)$(\\r\\n|\\r|\\n)*".toRegex(RegexOption.MULTILINE)) { match ->
                val quoteContent = match.groupValues[1]
                    .split("\n>")
                    .joinToString("\n")
                "<blockquote>${quoteContent.trim()}</blockquote>"
            },

            Pair(Regex("\n*\\|(.+?)\\|\n\\|(?:\\s*:?-+:?\\s*\\|)+\n((?:\\|.+?\\|\n)*)")) { match ->
                val header = match.groupValues[1].trim()
                val body = match.groupValues[2].trim()

                val headerHtml = header.split("|").map { it.trim() }.joinToString("") {
                    "<th class=\"table_split\">${it.trim()}</th>"
                }

                val bodyHtml = body.split("\n").filter { it.isNotBlank() }.joinToString("") { row ->
                    val cells = row.split("|").map { it.trim() }.filter { it.isNotBlank() }
                    val rowHtml = cells.joinToString("") {
                        "<td class=\"table_split\">${it}</td>"
                    }
                    "<tr>$rowHtml</tr>"
                }

                "<table style=\"border-collapse: collapse; width: 100%;\"><thead><tr>$headerHtml</tr></thead><tbody>$bodyHtml</tbody></table>"
            },

            Pair("^-{3,}$".toRegex(RegexOption.MULTILINE)) { match ->
                "<hr/>"
            },

            //at
            Pair("\\[@([^]]+)]\\(at:(\\d+)\\)".toRegex()) { match ->
                val username = match.groupValues[1]
                val userId = match.groupValues[2]
                "<a href=\"${Constants.BBS_URL}/user/$userId\">@$username</a>"
            },

            //image
            Pair("!\\[(.*?)]\\((.*?)\\)(\\r\\n|\\r|\\n)*".toRegex()) { match ->
                val dsp = match.groupValues[1]
                val url = match.groupValues[2]
                if (url.isEmpty() && dsp.isEmpty()) {
                    ""
                } else {
                    if (url == "s") {
                        //emoji
                        val item = EmotionManager.getEmotionById(dsp)
                        "<img class=\"emoji\" src=\"${item?.aPath}\" />"
                    } else {
                        val attachment = attachments
                            ?.find {
                                it.attachmentId.toString() == url.replace("i:", "")
                            }
                        if (attachment?.isImage == 1) {
                            imageViewerConfig.images.add(
                                ImageViewerConfig.ImageViewerItem(attachment.path, attachment.thumbnailUrl)
                            )
                            "<img class=\"attachimg\", " +
                                    "src=\"${attachment.thumbnailUrl.toBBSImgUrl()}\" " +
                                    "onclick=\"onImageClick('${attachment.thumbnailUrl}')\" " +
                                    "onload=\"applyStoredHeight(this)\" " +
                                    "alt=\"${attachment.description}\"/>"
                        } else {
                            imageViewerConfig.images.add(
                                ImageViewerConfig.ImageViewerItem(url, url)
                            )
                            "<img class=\"attachimg\", " +
                                    "src=\"${url}\" " +
                                    "onclick=\"onImageClick('${url}')\"" +
                                    "onload=\"applyStoredHeight(this)\"/>"
                        }
                    }
                }
            },

            //attachment
            Pair("\\[(.*?)]\\(a:(.*?)\\)(\\r\\n|\\r|\\n)*".toRegex()) { match ->
                val attachmentId = match.groupValues[2]
                val attachment = attachments?.find { it.attachmentId.toString() == attachmentId }
                getAttachmentHtml(context, attachment, imageViewerConfig)
            },

            //link
            Pair("\\[([^\\[]*?)]\\(([^)]*?)\\)".toRegex()) { match ->
                val text = match.groupValues[1]
                val url = match.groupValues[2]
                var href = url.ifEmpty { text }
                if (!Regex("^https?://").containsMatchIn(href)) {
                    href = Constants.BBS_URL.plus(if (href.startsWith("/")) "" else "/") + href
                }
                "<a href=\"$href\">$text</a>"
            },
        )

        var result = input.toString()
            //.replace("\n", "<br/>")
            //.replace("^\\s*(-{3,}|\\*{3,})\\s*\$".toRegex(RegexOption.MULTILINE), "<hr/>")
            //.replace("\\*", "PLACEHOLDER_STAR")
            .replace("\\~", "~")

        var matchOccurred: Boolean
        do {
            matchOccurred = false
            regexReplacements.forEach { (regex, replacement) ->
                result = regex.replace(result) { matchResult ->
                    matchOccurred = true
                    replacement.invoke(matchResult)
                }
            }
        } while (matchOccurred)

        result = result
            .replace("\n", "<br/>")
            //.replace("PLACEHOLDER_STAR", "*")

        return result
    }

    @JvmStatic
    private fun getAttachmentHtml(
        context: Context,
        attachment: AttachmentEntity?,
        imageViewerConfig: ImageViewerConfig,
    ): String {
        return if (attachment != null) {
            if (attachment.isImage == 1 || isImage(attachment.filename)) {
                imageViewerConfig.images.add(
                    ImageViewerConfig.ImageViewerItem(attachment.path, attachment.thumbnailUrl)
                )
                "<img class=\"attachimg\", " +
                        "src=\"${attachment.thumbnailUrl.toBBSImgUrl()}\" " +
                        "onclick=\"onImageClick('${attachment.thumbnailUrl}')\"" +
                        "onload=\"applyStoredHeight(this)\"" +
                        "alt=\"${attachment.description}\"/>"
            } else if (isVideo(attachment.filename)) {
                "<video class=\"video\" controls ${if (DataStore.videoAutoPlay) "autoplay muted" else ""}>" +
                    "<source src=\"${attachment.path.toBBSImgUrl()}\" type=\"video/${attachment.filename?.substringAfterLast(".")}\">" +
                    "Your browser does not support the video tag." +
                "</video>"
            } else if (isAudio(attachment.filename)) {
                "<div class=\"attachaudio\">" +
                    "<strong>${attachment.filename}</strong>" +
                    "<br/>" +
                    "<audio class=\"audio\" controls>" +
                        "<source src=\"${attachment.path.toBBSImgUrl()}\" type=\"audio/mpeg\">" +
                        "Your browser does not support the audio element." +
                    "</audio>" +
                "</div>"
            } else {
                val url = Constants.BBS_URL + attachment.downloadUrl
                "<div class=\"attachment\">" +
                    "<strong>${attachment.filename}</strong>" +
                    "<br/>" +
                    "<a href=\"${url}\" onclick=\"onAttachmentClick('${url}', '${attachment.filename}')\">${ContextCompat.getString(context, R.string.download)}</a>" +
                "</div>"
            }
        } else {
            ""
        }
    }

    @JvmStatic
    fun isVideo(name: String?): Boolean {
        if (name.isNullOrEmpty()) {
            return false
        }
        return name.endsWith(".mp4", true)
    }

    @JvmStatic
    fun isImage(name: String?): Boolean {
        if (name.isNullOrEmpty()) {
            return false
        }
        return name.endsWith(".jpg", true)
                || name.endsWith(".jpeg", true)
                || name.endsWith(".png", true)
    }

    @JvmStatic
    fun isAudio(name: String?): Boolean {
        if (name.isNullOrEmpty()) {
            return false
        }
        return name.endsWith(".mp3", true)
    }

    fun toM(input: String, attachments: List<AttachmentEntity>?): String {



        var res = input
        attachments?.forEach {
            if (it.isImage == 1) {
                res = res.replace(
                    "![${it.filename}](i:${it.attachmentId})",
//                    "![${it.filename}](${it.thumbnailUrl.toBBSImgUrl()})"
                    "[![${it.filename}](${it.thumbnailUrl.toBBSImgUrl()})](${it.thumbnailUrl.toBBSImgUrl()})"
                )
            }
        }

        return res
    }
}

