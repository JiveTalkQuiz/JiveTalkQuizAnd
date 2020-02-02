package com.bwillocean.jiveQuizTalk.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bwillocean.jiveQuizTalk.data.QuizRepository
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import io.reactivex.Single
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

    fun loadData() {
        val disposable = repo.getQuizList()
            .doOnSubscribe { eventStream.onNext(QuizEvent.LOADING_START) }
            .doOnTerminate { eventStream.onNext(QuizEvent.LOADING_END) }
            .doOnSuccess {
                dataStream.onNext(it)
            }.subscribe({}, {it.printStackTrace()})
    }
}