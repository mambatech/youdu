package com.duyin.bookshelf.utils

import com.duyin.bookshelf.MApplication

/**
created by edison 2020-02-29
 */
object SharePreferenceHelper {

    val preferences = MApplication.getConfigPreferences()

    fun getBoolean(key: String,default: Boolean): Boolean{
        return preferences.getBoolean(key,default)
    }

    fun setBoolean(key: String,value: Boolean){
        preferences.edit()
                .putBoolean(key, value)
                .apply()
    }


}