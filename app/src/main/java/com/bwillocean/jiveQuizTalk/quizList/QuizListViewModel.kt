package com.bwillocean.jiveQuizTalk.quizList

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bwillocean.jiveQuizTalk.data.QuizRepository
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
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
            if ((it+1) < (quiz?.quizList?.size ?: 0)) {
                quiz?.quizList?.get(it + 1)
            } else null
        }
    }

    fun setSelectedQuiz(quizItem: QuizItem) {
        lastSelectedQuiz = quizItem
    }
}