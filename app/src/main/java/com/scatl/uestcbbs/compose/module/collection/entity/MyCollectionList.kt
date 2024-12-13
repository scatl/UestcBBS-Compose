package com.scatl.uestcbbs.compose.module.collection.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by sca_tl at 2024/10/14 20:45:44
 */
@Parcelize
data class MyCollectionList(
    var ctid: Int,
    var name: String
) : Parcelable