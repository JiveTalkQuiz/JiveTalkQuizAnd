package com.bwillocean.jiveQuizTalk

import android.app.Application
import android.os.Handler
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.util.CrashUtils
import com.google.firebase.crashlytics.core.CrashlyticsCore

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var instance: MyApplication
        lateinit var handler: Handler

        fun async(callback: Runnable) {
            handler.post(callback)
        }

        fun async(delay: Long, callback: Runnable) {
            handler.postDelayed(callback, delay)
        }
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this, "ca-app-pub-4146232804662233~8970322408")
        instance = this
        handler = Handler(this.mainLooper)
    }
}