package com.bwillocean.jiveQuizTalk.quizList.view

import android.util.Log
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.quizList.QuizListActivity
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_list_activity.*

class AdView(val activity: BaseActivity, val viewMode: MainViewModel): BaseView(activity) {
    override fun bindViewModel(disposable: CompositeDisposable) {}

    override fun unbindViewModel() {}

    override fun onCreate() {
        super.onCreate()

        AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build().let { adRequest ->
            activity.adView.run {
                adListener = object: AdListener() {
                    override fun onAdFailedToLoad(code: Int) {
                        super.onAdFailedToLoad(code)
                        Log.e(QuizListActivity.TAG, "ad load error $code")
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