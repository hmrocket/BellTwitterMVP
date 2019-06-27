package com.bell.demo.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.IntRange

class AppConfig(context : Context) {

    private val FILE_NAME = "config_file"
    private val KEY_RADIUS = "radius"

    companion object {
        const val MIN_RADIUS = 5L // KM
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var radius : Int
        get() = sharedPreferences.getInt(KEY_RADIUS, MIN_RADIUS.toInt()) // default 5KM
        set(@IntRange(from = MIN_RADIUS, to = 6371000L) value)  = sharedPreferences.edit().putInt(KEY_RADIUS, value).apply()

}