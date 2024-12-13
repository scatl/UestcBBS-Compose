package com.scatl.uestcbbs.compose.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Created by sca_tl at 2024/8/19 14:00:43
 */
@OptIn(ExperimentalContracts::class)
fun <T> Collection<T>?.isNotNullAndEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullAndEmpty != null)
    }
    return !this.isNullOrEmpty()
}

fun <T> Collection<T>?.safeSubList(fromIndex: Int, toIndex: Int): List<T>? {
    if (this == null) {
        return null
    }

    val list = this.toList()

    val validFromIndex = fromIndex.coerceAtLeast(0)

    if (toIndex > list.size) {
        return list
    }

    val validToIndex = toIndex.coerceAtMost(list.size)
    return if (validFromIndex >= validToIndex) {
        null
    } else {
        list.subList(validFromIndex, validToIndex)
    }
}

@OptIn(ExperimentalContracts::class)
fun <K, V> Map<K, V>?.isNotNullAndEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullAndEmpty != null)
    }
    return !this.isNullOrEmpty()
}