package com.scatl.uestcbbs.compose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

/**
 * Created by sca_tl at 2024/7/15 22:30
 */
@Entity(tableName = "medals")
data class MedalDBEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val dsp: String?,
    val image: String?,
    val price: Int?,
    val type: Int?,
    val displayOrder: Int?,
    val expirationDays: Int?,
    val extCredit: String?,
)