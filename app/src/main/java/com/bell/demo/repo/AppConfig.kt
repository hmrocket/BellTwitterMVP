package com.bell.demo.repo

import android.content.Context
import android.content.SharedPreferences

class AppConfig(context : Context) {

    private val FILE_NAME = "config_file"
    private val KEY_RADIUS = "radius"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var radius : Int
        get() = sharedPreferences.getInt(KEY_RADIUS, 10) // default 10KM
        set(value)  = sharedPreferences.edit().putInt(KEY_RADIUS, value).apply()

}