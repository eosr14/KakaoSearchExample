package com.eosr14.example.kakao.common.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


fun String.convertDisplayDate(): String {
    return if (this.isEmpty()) {
        ""
    } else {
        this.convertIsoDate()?.let {
            return SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(it)
        } ?: ""
    }
}

fun String?.convertDisplayDetailDate(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        this.convertIsoDate()?.let {
            return SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.getDefault()).format(it)
        } ?: ""
    }
}

fun String.convertIsoDate(): Date? {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).parse(this)
}

fun Date?.isYesterday(): Boolean {
    return this?.let {
        DateUtils.isToday(it.time + DateUtils.DAY_IN_MILLIS)
    } ?: false
}

fun Date?.isToday(): Boolean {
    return this?.let {
        DateUtils.isToday(this.time)
    } ?: false
}