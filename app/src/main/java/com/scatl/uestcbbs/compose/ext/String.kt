package com.scatl.uestcbbs.compose.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.scatl.uestcbbs.compose.BuildConfig
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import androidx.core.graphics.toColorInt

/**
 * Created by sca_tl at 2024/8/19 13:55:55
 */
fun String?.toBBSImgUrl() = "${Constants.BBS_URL}/${this}"

fun String?.toMealUrl() = "${Constants.BBS_URL}/static/image/common/${this}"

fun String?.showToast(context: Context) {
    if (this != null) {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}

fun String?.copyToClipBoard(context: Context) {
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?)?.let { manager ->
        manager.setPrimaryClip(ClipData.newPlainText(BuildConfig.APPLICATION_ID, this ?: ""))
        manager.primaryClip?.let {
            if (it.getItemAt(0).text.toString() == this) {
                ContextCompat.getString(context, R.string.copy_content_success).showToast(context)
            } else {
                ContextCompat.getString(context, R.string.copy_content_fail).showToast(context)
            }
            return
        } ?: {
            ContextCompat.getString(context, R.string.copy_content_fail).showToast(context)
        }
    }
}

fun String?.hexToColor(default: Color = Color.Transparent): Color {
    return try {
        Color(this!!.toColorInt())
    } catch (e: Exception) {
        default
    }
}

fun String?.toIntOrElse(default: Int = 0) = try {
    this?.toIntOrNull() ?: default
} catch (e: Exception) {
    default
}

fun String?.removeAllBlank() = this
    ?.replace("\n", "")
    ?.replace("\t", "")
    ?.replace(" ", "")
    ?.replace("\u3000", "")

fun String?.safeSubstring(startIndex: Int, endIndex: Int): String? {
    if (this == null) {
        return null
    }

    return if (endIndex > this.length) {
        this.substring(startIndex)
    } else {
        this.substring(startIndex, endIndex)
    }
}

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullAndEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullAndEmpty != null)
    }
    return !this.isNullOrEmpty()
}