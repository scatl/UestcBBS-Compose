package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SystemSettingEntity(
    @Json(name = "medals")
    val medals: Medals? = Medals()
) {
    @JsonClass(generateAdapter = true)
    data class Medals(
        @Json(name = "value")
        val value: List<Value?>? = listOf(),
        @Json(name = "version")
        val version: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class Value(
            @Json(name = "description")
            val description: String? = "",
            @Json(name = "display_order")
            val displayOrder: Int? = 0,
            @Json(name = "expiration_days")
            val expirationDays: Int? = 0,
            @Json(name = "ext_credit")
            val extCredit: String? = "",
            @Json(name = "id")
            val id: Int? = 0,
            @Json(name = "image_path")
            val imagePath: String? = "",
            @Json(name = "name")
            val name: String? = "",
            @Json(name = "price")
            val price: Int? = 0,
            @Json(name = "type")
            val type: Int? = 0
        )
    }
}