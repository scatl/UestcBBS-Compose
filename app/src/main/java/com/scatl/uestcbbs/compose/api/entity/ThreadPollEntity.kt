package com.scatl.uestcbbs.compose.api.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ThreadPollEntity(
    @Json(name = "expiration")
    val expiration: Long? = 0,
    @Json(name = "is_image")
    val isImage: Boolean? = false,
    @Json(name = "max_choices")
    val maxChoices: Int? = 0,
    @Json(name = "multiple")
    val multiple: Boolean? = false,
    @Json(name = "options")
    val options: List<Option>? = listOf(),
    @Json(name = "show_voters")
    val showVoters: Boolean? = false,
    @Json(name = "visible")
    val visible: Boolean? = false,
    @Json(name = "voter_count")
    val voterCount: Int? = 0,
    @Json(name = "selected_options")
    val selected: List<Int>? = listOf()
): Parcelable {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Option(
        @Json(name = "display_order")
        val displayOrder: Int? = 0,
        @Json(name = "id")
        val id: Int? = 0,
        @Json(name = "text")
        val text: String? = "",
        @Json(name = "votes")
        val votes: Int? = 0,
        @Json(name = "voters")
        val voters: List<Int>? = listOf()
    ): Parcelable
}