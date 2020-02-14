package com.bwillocean.jiveQuizTalk.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bwillocean.jiveQuizTalk.Const
import com.bwillocean.jiveQuizTalk.MyApplication
import com.bwillocean.jiveQuizTalk.dialog.LoadingDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
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
                KEY_NAME_POINT, DEFAULT_POINT)
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

    fun createRewardAd(activity: Activity, showLoading: Boolean = true, loaded: (RewardedAd) -> Unit) {
        if (showLoading)
            LoadingDialog.showLoading(activity, 3*1000)
        val rewardedAd = RewardedAd(activity, Const.AD_UNIT_REWARD_TEST)
        val adLoadCallback: RewardedAdLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() { // Ad successfully loaded.
                LoadingDialog.hideLoading()
                Log.d("ad", "[reward ad] load done")
                loaded(rewardedAd)
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) { // Ad faild to load.
                LoadingDialog.hideLoading()
                Log.e("ad", "[reward ad] error $errorCode")
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    fun createFullAd(activity: Activity, callback: (InterstitialAd) -> Unit){
        LoadingDialog.showLoading(activity, 3*1000)

        val interstitialAd = InterstitialAd(activity)
        interstitialAd.adUnitId = Const.AD_UNIT_FULL_TEST
        interstitialAd.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Log.e("TEST", "onAdFailedToLoad $p0")
                LoadingDialog.hideLoading()
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