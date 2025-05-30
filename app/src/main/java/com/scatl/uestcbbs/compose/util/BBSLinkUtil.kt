package com.scatl.uestcbbs.compose.util

import android.net.Uri
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.toIntOrElse

object BBSLinkUtil {

    @JvmStatic
    fun getLinkType(url: String?): LinkType {
        if (url.isNullOrEmpty()) {
            return LinkType.Unknown
        }

        val realUrl = "https://bbs.uestc.edu.cn/"
            .plus(url
                .replace("amp;", "")
                .replace("(", "")
                .replace(")", "")
                .replace("https://bbs.uestc.edu.cn/", "")
            )

        if (realUrl.startsWith("https://bbs.uestc.edu.cn/star/api/v1/attachment/")) {
            return LinkType.Attachment(realUrl)
        }

        val params = getParameters(realUrl)
        val paths = getPathSegments(realUrl)

        if (url.contains("bbs.stuhome.net/read.php")) {
            return LinkType.ThreadDetail(params["tid"].toIntOrElse())
        } else if(url.contains("plugin.php?id=rnreg:resetpassword")) {
            return LinkType.ResetPsw
        }

        if (params["mod"].isNotNullAndEmpty()) {
            when(params["mod"]) {
                "viewthread" -> {
                    return LinkType.ThreadDetail(params["tid"].toIntOrElse())
                }
                "space" -> {
                    val uid = params["uid"]
                    val `do` = params["do"]
                    val view = params["view"]

                    return if ("friend" == `do` && "blacklist" == view) {
                        LinkType.BlackList
                    } else if (uid.isNotNullAndEmpty()) {
                        LinkType.UserDetail(params["uid"].toIntOrElse())
                    } else {
                        LinkType.Unknown
                    }
                }
                "forumdisplay" -> {
                    return LinkType.ForumDetail(params["fid"].toIntOrElse())
                }
                "collection" -> {
                    return LinkType.Collection(params["ctid"].toIntOrElse())
                }
                "task" -> {
                    return LinkType.Task(params["id"].toIntOrElse())
                }
                "magic" -> {
                    return LinkType.Magic(params["id"].toIntOrElse())
                }
                "medal" -> {
                    return LinkType.Medal
                }
                "spacecp" -> {
                    val ac = params["ac"]
                    if ("credit" == ac) {
                        return LinkType.CreditHistory
                    } else {
                        return LinkType.Unknown
                    }
                }
                "redirect" -> {
                    if (params["goto"] == "findpost") {
                        return LinkType.ThreadDetail(
                            id = params["ptid"].toIntOrElse(),
                            pid = params["pid"].toIntOrElse(-1)
                        )
                    }
                    return LinkType.Unknown
                }
//            "misc" -> {
//                if (params["action"] == "viewvote") {
//                    linkInfo.apply {
//                        id = NumberUtil.parseInt(params["tid"])
//                        type = LinkInfo.LinkType.VIEW_VOTER
//                    }
//                }
//            }
                else -> {
                    return LinkType.Unknown
                }
            }
        } else if (paths.isNotNullAndEmpty()) {
            when (paths[0]) {
                "thread" -> {
                    return LinkType.ThreadDetail(paths.getOrNull(1).toIntOrElse())
                }
                "goto" -> {
                    return LinkType.ThreadDetail(
                        id = 0,
                        pid = paths.getOrNull(1).toIntOrElse()
                    )
                }
                "user" -> {
                    return if (paths.getOrNull(1) == "name") {
                        LinkType.UserDetail(
                            id = 0,
                            name = paths.getOrNull(1) ?: ""
                        )
                    } else {
                        LinkType.UserDetail(
                            id = paths.getOrNull(1).toIntOrElse(),
                            name = ""
                        )
                    }
                }
                else -> {
                    return LinkType.Unknown
                }
            }
        }

        return LinkType.Unknown
    }

    @JvmStatic
    fun getParameters(url: String?): HashMap<String, String?> {
        val parameters = HashMap<String, String?>()

        runCatching {
            val uri = Uri.parse(url)
            uri.queryParameterNames.let {
                val iterator = it.iterator()
                while (iterator.hasNext()) {
                    val key = iterator.next() as String
                    parameters[key] = uri.getQueryParameter(key)
                }
            }
        }.onFailure {
            return parameters
        }

        return parameters
    }

    @JvmStatic
    fun getPathSegments(url: String?): List<String> {
        runCatching {
            val uri = Uri.parse(url)
            return uri.pathSegments
        }
        return listOf()
    }
}

sealed class LinkType {
    data class ThreadDetail(val id: Int, val pid: Int = -1): LinkType()
    data class UserDetail(val id: Int, val name: String = ""): LinkType()
    data class ForumDetail(val id: Int): LinkType()
    data object BlackList: LinkType()
    data class Task(val id: Int): LinkType()
    data class Collection(val id: Int): LinkType()
    data object CreditHistory: LinkType()
    data object ResetPsw: LinkType()
    data object Medal: LinkType()
    data class Magic(val id: Int): LinkType()
    data class Attachment(val url: String): LinkType()
    data object Unknown: LinkType()
}