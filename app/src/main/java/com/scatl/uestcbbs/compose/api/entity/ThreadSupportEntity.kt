package com.scatl.uestcbbs.compose.api.entity

/**
 * Created by sca_tl at 2024/8/16 14:08:57
 */
data class ThreadSupportEntity(
    /**
     * 0：增加
     * 1：减少
     * 2：已评价过，但是可以继续当前操作
     * 3：已评价，不可进行操作
     */
    var type: Int?,

    var support: Boolean
)