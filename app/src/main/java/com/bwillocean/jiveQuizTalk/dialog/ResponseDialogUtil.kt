package com.bwillocean.jiveQuizTalk.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bwillocean.jiveQuizTalk.MyApplication
import com.bwillocean.jiveQuizTalk.R

object ResponseDialogUtil {
    fun responseDialog(activity: Activity, currect: Boolean, cancelListener: DialogInterface.OnCancelListener) {
        val dialog = Dialog(activity, R.style.TransparentDialogStyle)
        dialog.setContentView(if(currect) R.layout.response_dialog else R.layout.response_dailog_fail)
        val button = dialog.findViewById<ImageView>(R.id.correct_icon)
        button.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setOnCancelListener(cancelListener)
        dialog.show()

        MyApplication.async((1.5*1000).toLong(), Runnable {
            if(dialog.isShowing && dialog.context != null && !activity.isDestroyed && !activity.isFinishing) {
                dialog.cancel()
            }
        })
    }
}