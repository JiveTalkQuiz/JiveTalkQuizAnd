package com.bwillocean.jiveQuizTalk.quizList.view

import android.content.Intent
import android.os.Build.VERSION_CODES.P
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.data.model.QuizSelection
import com.bwillocean.jiveQuizTalk.quiz.QuizActivity
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import com.bwillocean.jiveQuizTalk.quizList.QuizEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_list_activity.*
import kotlin.math.roundToInt

class QuizListView(private val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity),
    RecyclerView.OnItemTouchListener {
    val spanCount = calculateSpanCount(75)
    val quizAdapter = QuizAdapter()
    lateinit var recyclerView: RecyclerView

    override fun bindViewModel(disposable: CompositeDisposable) {
        disposable.add(viewModel.dataStream
            .onErrorReturn { Quiz(listOf(QuizItem("", "", listOf(QuizSelection("", false))))) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                quizAdapter.setQuiz(it)
            },{}))

        disposable.add(viewModel.eventStream
            .onErrorReturn { QuizEvent.NONE }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                when(it) {
                    QuizEvent.LOADING_START -> {
                        //TODO loading
                    }
                    QuizEvent.LOADING_END -> {
                        //TODO loading
                    }
                }
            }, {}))
    }

    override fun unbindViewModel() {

    }

    override fun onCreate() {
        super.onCreate()

        recyclerView = activity.grid.apply {
            layoutManager = GridLayoutManager(activity, spanCount)
            setHasFixedSize(true)
            //addItemDecoration(SpaceItemDecoration(DpUtils.dip2px(activity, 25/2f)))
            adapter = quizAdapter
        }

        recyclerView.addOnItemTouchListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.removeOnItemTouchListener(this)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        rv.findChildViewUnder(e.x, e.y)?.let { child ->
            val position = rv.getChildAdapterPosition(child)

            if(position != RecyclerView.NO_POSITION) {
                quizAdapter.getItem(position)?.let { quizItem ->
                    viewModel.startQuizDetail(activity, quizItem)
                }
            }
        }
        return false
    }

    private fun calculateSpanCount(itemDpWidth: Int): Int {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = activity.resources.displayMetrics.density
        val dpWidth = outMetrics.widthPixels / density
        return (dpWidth / itemDpWidth).roundToInt()
    }
}