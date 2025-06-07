package com.scatl.uestcbbs.compose.api.entity.wealth

data class BaseWealthEntity(
    var creditCount: Int = 0,
    var waterCount: Int = 0,
    var couponCount: Int = 0,
    var prestigeCount: Int = 0,
    var creditRule: String? = "总积分=发帖数X0.1+精华帖数X10+威望X5+在线时间(小时)"
)
