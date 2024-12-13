package com.scatl.uestcbbs.compose.api.entity.user

import com.scatl.uestcbbs.compose.api.entity.CollectionEntity
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCollectionEntity(
    @Json(name = "collections")
    val collections: List<CollectionEntity>? = listOf(),
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "rows")
    val rows: List<Row>? = listOf(),
    @Json(name = "total")
    val total: Int? = 0
) {

    @JsonClass(generateAdapter = true)
    data class Row(
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "description")
        val description: String? = "",
        @Json(name = "favorite_id")
        val favoriteId: Int? = 0,
        @Json(name = "space_uid")
        val spaceUid: Int? = 0,
        @Json(name = "target_id")
        val targetId: Int? = 0,
        @Json(name = "target_type")
        val targetType: String? = "",
        @Json(name = "thread_details")
        val threadDetails: CommonThreadEntity? = null,
        @Json(name = "title")
        val title: String? = ""
    ): SwipeRefreshItem {
        override var isStickerHeader = false
    }
}