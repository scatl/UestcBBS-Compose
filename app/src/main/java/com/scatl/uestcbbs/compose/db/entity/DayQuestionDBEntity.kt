package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/9/25 16:11:45
 */
@Entity(
    tableName = "day_question",
    indices = [
        Index(value = ["question"], unique = true)
    ]
)
@JsonClass(generateAdapter = true)
data class DayQuestionDBEntity (
    @PrimaryKey(autoGenerate = true)
    @Json(ignore = true)
    val id: Int = 0,
    var question: String,
    var answer: String,
    var deletable: Boolean = false
): SwipeRefreshItem {
    override var isStickerHeader = false
}