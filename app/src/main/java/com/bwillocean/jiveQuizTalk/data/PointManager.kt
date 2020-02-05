package com.bwillocean.jiveQuizTalk.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bwillocean.jiveQuizTalk.MyApplication
import com.bwillocean.jiveQuizTalk.dialog.LoadingDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import io.reactivex.subjects.PublishSubject

object PointManager {
    const val DEFAULT_POINT = 15
    const val FAIL_POINT = 1
    const val HINT_POINT = 2
    const val AD_POINT = 15
    const val USE_POINT = 1
    const val PREF_NAME = "point_pref"
    const val KEY_NAME_POINT = "point"
    var point: Int
        get() {
            return MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(
                KEY_NAME_POINT, 100)
        }
        set(value) {
            pointStream.onNext(
                point
            )

            MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(
                KEY_NAME_POINT,
                kotlin.math.max(0, value)
            ).apply()
        }

    val pointStream = PublishSubject.create<Int>()

    fun createFullAd(activity: Activity, callback: (InterstitialAd) -> Unit){
        LoadingDialog.showLoading(activity)

        val interstitialAd = InterstitialAd(activity)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Log.e("TEST", "onAdFailedToLoad $p0")
            }

            override fun onAdClosed() { // Load the next interstitial.
                point += AD_POINT
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                MyApplication.async(Runnable {
                    callback(interstitialAd)
                    LoadingDialog.hideLoading()
                })
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build())
    }
}