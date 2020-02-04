package com.bwillocean.jiveQuizTalk.quizList

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.quiz.QuizActivity
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Def.ACTIVITY_REQUEST_CODE_QUIZ_DETAIL && resultCode == Def.ACTIVITY_RESPONSE_CODE_NEXT_QUIZ) {
            mainViewModel.nextQuiz()?.let { quizItem ->
                Intent(this, QuizActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(QuizActivity.EXTRAS_KEY, quizItem)
                }.run {
                    startActivityForResult(this, Def.ACTIVITY_REQUEST_CODE_QUIZ_DETAIL)
                }
            } ?: run {
                Toast.makeText(this, "마지막 문제", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
