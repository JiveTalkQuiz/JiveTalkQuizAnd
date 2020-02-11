package com.bwillocean.jiveQuizTalk

import android.content.Intent
import android.os.Bundle
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.quizList.QuizListActivity

class SplashActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        MyApplication.async(1500, Runnable {
            startActivity(Intent(this, QuizListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            MyApplication.async( Runnable {
                finish()
            })
        })
    }
}