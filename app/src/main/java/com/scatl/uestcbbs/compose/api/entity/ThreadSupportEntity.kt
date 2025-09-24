package com.scatl.uestcbbs.compose.api.entity

/**
 * Created by sca_tl at 2024/8/16 14:08:57
 */
data class ThreadSupportEntity(
    var type: Int?, //0：增加  1：减少  3：已评价
    var support: Boolean
)