package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vote_posts")
data class VotedPostDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var pid: String?,
    var against: Boolean?,
    var support: Boolean?,
)
