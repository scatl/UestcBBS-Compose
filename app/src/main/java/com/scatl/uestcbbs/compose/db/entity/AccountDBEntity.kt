package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by sca_tl at 2024/7/31 22:29
 */
@Entity(tableName = "account")
data class AccountDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String?,
    val uid: String?,
    val token: String?,
    val cookies: List<String>?,
    val icon: String?,
    val signedIn: Boolean?
)