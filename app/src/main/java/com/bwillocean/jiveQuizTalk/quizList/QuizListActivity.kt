package com.bwillocean.jiveQuizTalk.quizList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.quizList.view.AdView
import com.bwillocean.jiveQuizTalk.quizList.view.PointView
import com.bwillocean.jiveQuizTalk.quizList.view.QuizListView
import com.bwillocean.jiveQuizTalk.quizList.view.SoundView
import com.bwillocean.jiveQuizTalk.sound.SoundManager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.quiz_list_activity.*
import kotlin.math.absoluteValue

class QuizListActivity : BaseActivity() {
    companion object {
        const val TAG = "QuizListActivity"
    }

    lateinit var mainViewModel: MainViewModel
    lateinit var adView: AdView
    lateinit var mainView: QuizListView
    lateinit var pointView: PointView
    lateinit var soundView: SoundView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_list_activity)

        appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offsetAlpha = appbarLayout.y / appbarLayout.totalScrollRange
            Log.v(TAG, "alpha=$offsetAlpha")
            placeholder.alpha = 1 - offsetAlpha.absoluteValue
            toolbar.alpha = offsetAlpha.absoluteValue
        })

        mainViewModel = ViewModelProvider(this, ViewModelFactory(this))[MainViewModel::class.java]

        adView = AdView(this, mainViewModel)
        mainView = QuizListView(this, mainViewModel)
        pointView = PointView(this, mainViewModel)
        soundView = SoundView(this, mainViewModel)

        mainViewModel.loadData()

        SoundManager.playBg()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Def.ACTIVITY_REQUEST_CODE_QUIZ_DETAIL && resultCode == Def.ACTIVITY_RESPONSE_CODE_NEXT_QUIZ) {
            mainViewModel.nextQuiz()?.let { quizItem ->
                mainViewModel.startQuizDetail(this, quizItem)
            } ?: run {
                Toast.makeText(this, "문제를 모두 풀었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
