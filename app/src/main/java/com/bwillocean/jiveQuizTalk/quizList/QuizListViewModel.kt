package com.bwillocean.jiveQuizTalk.quizList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.data.QuizRepository
import com.bwillocean.jiveQuizTalk.data.SolveManager
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.quiz.QuizActivity
import com.bwillocean.jiveQuizTalk.sound.SoundManager
import io.reactivex.subjects.BehaviorSubject

enum class QuizEvent {
    LOADING_START,
    LOADING_END,
    NONE
}
class MainViewModel(val context: Context) : ViewModel() {
    private val repo = QuizRepository(context)

    val dataStream = BehaviorSubject.create<Quiz>()
    val eventStream = BehaviorSubject.create<QuizEvent>()
    private  var quiz: Quiz? = null
    private var lastSelectedQuiz: QuizItem? = null

    fun loadData() {
        val disposable = repo.getQuizList()
            .doOnSubscribe { eventStream.onNext(QuizEvent.LOADING_START) }
            .doOnTerminate { eventStream.onNext(QuizEvent.LOADING_END) }
            .doOnSuccess {
                quiz = it
                dataStream.onNext(it)
            }.subscribe({}, {it.printStackTrace()})
    }

    fun nextQuiz(): QuizItem? {
        return quiz?.quizList?.indexOfFirst {
            it == lastSelectedQuiz
        }?.let {
            var index = it + 1
            var quizItem: QuizItem? = null
            do {
                quizItem = quiz?.quizList?.get(index)
                index++
                Log.d(QuizListActivity.TAG, "quiz=${quizItem?.word} id=${quizItem?.id}")
            } while (quizItem != null && SolveManager.checkQuizResult(quizItem.id))

            if (lastSelectedQuiz == quizItem) {
                null
            } else {
                quizItem
            }
        }
    }

    fun startQuizDetail(activity: Activity, quizItem: QuizItem) {
        lastSelectedQuiz = quizItem

        val passed = SolveManager.checkQuizResult(quizItem.id)
        if(!passed) {
            if (PointManager.point < PointManager.USE_POINT) {
                SoundManager.noMoney()
                Toast.makeText(activity, "포인트를 충전해 주세요", Toast.LENGTH_SHORT).show()
                return
            }
            PointManager.point -= PointManager.USE_POINT
        }

        Intent(activity, QuizActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.putExtra(QuizActivity.EXTRAS_KEY, quizItem)
        }.run {
            SoundManager.quizStart()
            activity.startActivityForResult(this, Def.ACTIVITY_REQUEST_CODE_QUIZ_DETAIL)
        }
    }
}