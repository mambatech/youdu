package com.duyin.bookshelf.todo.widget.modialog

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.duyin.bookshelf.R
import com.duyin.bookshelf.todo.help.PrivacyAndUserLienceHelper

/**
created by edison 2020-02-23
 */
class DisclaimerDialog(dialogCallBack: DisclaimerDialogCallBack,context: Context) : BaseDialog(context) {

    public interface DisclaimerDialogCallBack{
        fun onAccessClick()

        fun onNotAccessClick()
    }

    init {
        setContentView(R.layout.dialog_disclaimer)
        setProtocolSpan(findViewById(R.id.tv_term))

        findViewById<TextView>(R.id.tv_no_access).setOnClickListener {
            dialogCallBack.onNotAccessClick()
        }

        findViewById<TextView>(R.id.tv_access).setOnClickListener {
            dialogCallBack.onAccessClick()
        }
    }

    /**
     * 设置privacy 和 license span跳转处理
     */
    private fun setProtocolSpan(tv: TextView) {
        val resource = context.resources
        val color = resource.getColor(R.color.access_color)
        val privacy = resource.getString(R.string.privacy_hint)
        val license = resource.getString(R.string.user_license_hint)
        val totalTerm = resource.getString(R.string.user_privacy_dialog_term)
        val protocolSpanStr = SpannableString(totalTerm)

        val privacyStart = totalTerm.indexOf(privacy)
        val licenseStart = totalTerm.indexOf(license)
        protocolSpanStr.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                PrivacyAndUserLienceHelper.gotoPrivacyActivity(context)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
                ds.isUnderlineText = false
            }
        }, privacyStart, privacy.length + privacyStart, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        protocolSpanStr.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                PrivacyAndUserLienceHelper.gotoUserLicenseActivity(context)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
                ds.isUnderlineText = false
            }
        }, licenseStart, license.length + licenseStart, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        tv.text = protocolSpanStr
        tv.highlightColor = resource.getColor(android.R.color.transparent)
        tv.movementMethod = LinkMovementMethod.getInstance()
    }





}