package com.scatl.uestcbbs.compose.module.magic.entity

/**
 * Created by sca_tl at 2024/9/26 10:54:04
 */
data class MagicDetailEntity(
    var icon: String? = null,
    var name: String? = null,
    var dsp: String? = null,
    var originalPrice: String? = null,
    var discountPrice: String? = null,
    var mineWaterDrop: String? = null,
    var weight: String? = null,
    var availableWeight: String? = null,
    var stock: String? = null,
    var otherInfo: String? = null
)