package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by sca_tl at 2023/8/23 16:23
 */
@Entity(tableName = "snapshot")
data class SnapshotDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tid: String?,
    val subject: String?,
)