package com.bwillocean.jiveQuizTalk.quizList.view

import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.data.ScoreManager
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.data.model.QuizSelection
import com.bwillocean.jiveQuizTalk.dialog.LoadingDialog
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import com.bwillocean.jiveQuizTalk.quizList.QuizEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_list_activity.*
import kotlin.math.roundToInt

class QuizListView(private val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity) {
    val spanCount = calculateSpanCount(75)
    val quizAdapter = QuizAdapter()
    lateinit var recyclerView: RecyclerView

    override fun bindViewModel(disposable: CompositeDisposable) {
        disposable.add(viewModel.dataStream
            .onErrorReturn { Quiz(listOf(QuizItem(-1, "", listOf(QuizSelection("", false))))) }
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
                        LoadingDialog.showLoading(activity)
                    }
                    QuizEvent.LOADING_END -> {
                        LoadingDialog.hideLoading()
                    }
                }
            }, {}))

        disposable.add(ScoreManager.scoreStream.observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                quizAdapter.notifyDataSetChanged()
            }, {}))
    }

    override fun unbindViewModel() {

    }

    override fun onCreate() {
        super.onCreate()

        recyclerView = activity.grid.apply {
            layoutManager = GridLayoutManager(activity, spanCount).also {
                it.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (quizAdapter.getItemViewType(position) == QuizAdapter.TYPE_HEADER) {
                            spanCount
                        } else {
                            1
                        }
                    }
                }
            }
            setHasFixedSize(true)
            adapter = quizAdapter
        }

        quizAdapter.clickListener = View.OnClickListener { view ->
            (view?.tag as? QuizItem)?.let { quizItem ->
                viewModel.startQuizDetail(activity, quizItem)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        quizAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
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