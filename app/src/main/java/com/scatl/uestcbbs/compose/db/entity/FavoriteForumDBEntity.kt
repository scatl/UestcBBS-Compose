package com.scatl.uestcbbs.compose.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Created by tanlei02 at 2024/11/20 16:48:05
 */
@Entity(tableName = "favorite_forum")
@Parcelize
data class FavoriteForumDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var forumName: String?,
    var categoryName: String?,
    var forumId: Int?,
    var categoryId: Int?
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FavoriteForumDBEntity) return false
        return forumId == other.forumId && categoryId == other.categoryId
    }

    override fun hashCode(): Int {
        var result = forumId ?: 0
        result = 31 * result + (categoryId ?: 0)
        return result
    }
}