package com.bwillocean.jiveQuizTalk

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.util.CrashUtils
import com.google.firebase.crashlytics.core.CrashlyticsCore

class MyApplication: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this, "ca-app-pub-4146232804662233~8970322408")
    }
}