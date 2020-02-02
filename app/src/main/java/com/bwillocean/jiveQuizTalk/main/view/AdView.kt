package com.bwillocean.jiveQuizTalk.main.view

import android.util.Log
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.main.MainActivity
import com.bwillocean.jiveQuizTalk.main.MainViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class AdView(val activity: BaseActivity, val viewMode: MainViewModel): BaseView(activity) {
    override fun bindViewModel(disposable: CompositeDisposable) {}

    override fun unbindViewModel() {}

    override fun onCreate() {
        super.onCreate()

        AdRequest.Builder().build().let { adRequest ->
            activity.adView.run {
                adListener = object: AdListener() {
                    override fun onAdFailedToLoad(code: Int) {
                        super.onAdFailedToLoad(code)
                        Log.e(MainActivity.TAG, "ad load error $code")
                    }
                }
                loadAd(adRequest)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.adView.adListener = null
    }
}