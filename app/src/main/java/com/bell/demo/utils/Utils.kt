package com.bell.demo.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    const val INVALID_DATE: Long = -1
    private val DATE_TIME_RFC822 : SimpleDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)

    fun apiTimeToLong(apiTime: String?): Long {
        if (apiTime == null) return INVALID_DATE

        try {
            return DATE_TIME_RFC822.parse(apiTime).time
        } catch (e: ParseException) {
            return INVALID_DATE
        }

    }

    fun formatTime(time: String): CharSequence? {
        val createdAt = apiTimeToLong(time)
        if (createdAt != INVALID_DATE) {
            return DateUtils.getRelativeTimeSpanString(createdAt)
        }

        return null
    }
}