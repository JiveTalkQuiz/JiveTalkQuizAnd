package com.bwillocean.jiveQuizTalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bwillocean.jiveQuizTalk.data.QuizRepository
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    val repo = QuizRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adRequest = AdRequest.Builder().build()
        adView.adListener = object: AdListener() {
            override fun onAdFailedToLoad(code: Int) {
                super.onAdFailedToLoad(code)
                Log.e(TAG, "ad load error $code")
            }
        }
        adView.loadAd(adRequest)

        val disposable = repo.getQuizList().subscribe({
            it.quizList.forEach {
                Log.d(TAG, "item=${it.word}")
                it.selection.forEach {
                    Log.d(TAG, "selection=${it.statement}-${it.isCorrect}")
                }
            }
        },{it.printStackTrace()})
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.adListener = null
    }
}
