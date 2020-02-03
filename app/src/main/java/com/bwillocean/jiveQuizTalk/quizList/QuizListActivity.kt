package com.bwillocean.jiveQuizTalk.quizList

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.quizList.view.AdView
import com.bwillocean.jiveQuizTalk.quizList.view.QuizListView

class QuizListActivity : BaseActivity() {
    companion object {
        const val TAG = "QuizListActivity"
    }

    lateinit var mainViewModel: MainViewModel
    lateinit var adView: AdView
    lateinit var mainView: QuizListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_list_activity)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(this))[MainViewModel::class.java]

        adView = AdView(this, mainViewModel)
        mainView = QuizListView(this, mainViewModel)

        mainViewModel.loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
