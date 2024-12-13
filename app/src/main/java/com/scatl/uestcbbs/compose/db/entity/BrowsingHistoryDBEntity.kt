package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/10 19:25:09
 */
@Entity(
    tableName = "browsing_history",
    indices = [Index(value = ["threadId"], unique = true)]
)
data class BrowsingHistoryDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var threadId: String?,
    var forumId: String?,
    var forumName: String?,
    var authorId: String?,
    var authorName: String?,
    var subject: String?,
    var summary: String?,
    var dateLine: Int?,
    var lastBrowserDate: Long?
): SwipeRefreshItem {
    override var isStickerHeader = false
}