package com.scatl.uestcbbs.compose.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

/**
 * Created by sca_tl at 2024/7/11 18:56:36
 * @param timestamp seconds
 */
fun formatTimestamp(timestamp: Int?, context: Context): String {
    if (timestamp == null) {
        return ""
    }
    val currentSeconds = System.currentTimeMillis() / 1000
    val diff = currentSeconds - timestamp

    val currentDate = LocalDate.now()
    val timestampDateTime = Instant
        .ofEpochSecond(timestamp.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val timestampDate = timestampDateTime.toLocalDate()

    return when {
        diff < 60 -> {
            ContextCompat.getString(context, R.string.time_just_now)
        }
        diff < 60 * 60 -> {
            ContextCompat.getContextForLanguage(context).getString(R.string.time_min_ago, diff / 60)
        }
        currentDate == timestampDate -> {
            DateTimeFormatter.ofPattern("HH:mm").format(timestampDateTime)
        }
        currentDate.year == timestampDate.year -> {
            DateTimeFormatter.ofPattern(ContextCompat.getString(context, R.string.time_format_short)).format(timestampDateTime)
        }
        else -> {
            DateTimeFormatter.ofPattern(ContextCompat.getString(context, R.string.time_format_long)).format(timestampDateTime)
        }
    }
}

fun formatTimestampYMD(timestamp: Int?, context: Context): String {
    return formatTimestampYMD(timestamp.toIntOrElse() * 1000L, context)
}

fun formatTimestampYMD(timestamp: Long?, context: Context): String {
    if (timestamp == null) {
        return ""
    }

    val timestampDateTime = Instant
        .ofEpochMilli(timestamp.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    return DateTimeFormatter.ofPattern(ContextCompat.getString(context, R.string.time_format_ymd)).format(timestampDateTime)
}

fun formatTimestampYMDHMS(timestamp: Int?, context: Context): String {
    return formatTimestampYMDHMS(timestamp.toIntOrElse() * 1000L, context)
}

fun formatTimestampYMDHMS(timestamp: Long?, context: Context): String {
    if (timestamp == null) {
        return ""
    }

    val timestampDateTime = Instant
        .ofEpochMilli(timestamp.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    return DateTimeFormatter.ofPattern(ContextCompat.getString(context, R.string.time_format_ymdhms)).format(timestampDateTime)
}

fun calculateDays(timestamp: Int?): Long {
    if (timestamp == null) {
        return 0
    }
    val currentTimestamp = Instant.now().epochSecond

    val registrationDate = Instant.ofEpochSecond(timestamp.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val currentDate = Instant.ofEpochSecond(currentTimestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    return ChronoUnit.DAYS.between(registrationDate, currentDate)
}