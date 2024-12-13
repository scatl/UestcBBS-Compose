package com.scatl.uestcbbs.compose.ext

import com.scatl.uestcbbs.compose.Constants

/**
 * Created by sca_tl at 2024/8/19 13:58:51
 */
fun Int?.toAvatarUrl(): String = if (this == null || this == 0) {
    "https://bbs.uestc.edu.cn/assets/avatar-anonymous-HpYjPD08.png"
} else {
    "${Constants.BBS_URL}/uc_server/avatar.php?uid=${this}&size=middle"
}

fun Int?.toIntOrElse(default: Int = 0) = this ?: default