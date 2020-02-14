package com.bwillocean.jiveQuizTalk.quizList.view

import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bwillocean.jiveQuizTalk.R
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


class QuizListView(private val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity) {
    val spanCount = calculateSpanCount(70)
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

        val spacing = (listviewWidth() - (activity.resources.getDimensionPixelOffset(R.dimen.item_width) * spanCount)) / (spanCount - 1)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing))

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

    private fun listviewWidth(): Int {
        return displayWidth() - (activity.resources.getDimensionPixelOffset(R.dimen.main_list_margin) * 2)
    }

    private fun displayWidth(): Int {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    private fun calculateSpanCount(itemDpWidth: Int): Int {
        val density = activity.resources.displayMetrics.density
        val dpWidth = (displayWidth() / density) - 60
        return (dpWidth / itemDpWidth).toInt()
    }
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
        /*if (position >= spanCount) {
            outRect.top = spacing
        }*/
        outRect.top = spacing
        Log.v("test","$position is left ${outRect.left} right ${outRect.right} spacing $spacing")
    }
}