package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

@JsonClass(generateAdapter = true)
data class DarkRoomEntity(
    @Json(name = "data")
    var `data`: Map<Int, Item>? = mutableMapOf(),
    @Json(name = "message")
    var message: Message? = Message()
) {
    @JsonClass(generateAdapter = true)
    data class Message(
        @Json(name = "cid")
        var cid: String? = "",
        @Json(name = "dataexist")
        var dataexist: String? = ""
    )

    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "action")
        var action: String? = "",
        @Json(name = "cid")
        var cid: String? = "",
        @Json(name = "dateline")
        var dateline: String? = "",
        @Json(name = "groupexpiry")
        var groupexpiry: String? = "",
        @Json(name = "operator")
        var `operator`: String? = "",
        @Json(name = "operatorid")
        var operatorid: String? = "",
        @Json(name = "reason")
        var reason: String? = "",
        @Json(name = "uid")
        var uid: String? = "",
        @Json(name = "username")
        var username: String? = "",

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem
}