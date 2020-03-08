package com.duyin.bookshelf.bean

import androidx.annotation.DrawableRes
import com.flyco.tablayout.listener.CustomTabEntity

/**
created by edison 2020-02-28
 */
class BookTabEntity(val title: String,
                    @DrawableRes val selectedIcon: Int,
                    @DrawableRes val unSelectedIcon: Int) : CustomTabEntity {


    override fun getTabUnselectedIcon(): Int {
        return unSelectedIcon
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabTitle(): String {
        return title
    }
}