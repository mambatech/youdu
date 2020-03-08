package com.duyin.bookshelf.todo.help

import android.content.Context
import android.content.Intent
import com.duyin.bookshelf.R
import com.duyin.bookshelf.todo.constant.AppConstant
import com.duyin.bookshelf.todo.view.activity.CommonWebActivity

/**
created by edison 2020-02-27
 */
object PrivacyAndUserLienceHelper {

    fun gotoPrivacyActivity(context: Context) {
        val intent = Intent(context, CommonWebActivity::class.java)
        intent.putExtra(CommonWebActivity.KEY_URL, AppConstant.PRIVACY_POLICY_OTHER_URL)
        intent.putExtra(CommonWebActivity.KEY_TITLE, context.getString(R.string.privacy_title))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun gotoUserLicenseActivity(context: Context) {
        val intent = Intent(context, CommonWebActivity::class.java)
        intent.putExtra(CommonWebActivity.KEY_URL, AppConstant.USER_LICENSE_OTHER_URL)
        intent.putExtra(CommonWebActivity.KEY_TITLE, context.getString(R.string.user_license_title))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}