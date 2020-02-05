package com.bwillocean.jiveQuizTalk.quizList.view

import android.util.Log
import android.view.View
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_activity.*

class PointView (val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity),
    View.OnClickListener {

    override fun onCreate() {
        super.onCreate()

        activity.point_text.text = PointManager.point.toString()

        activity.point_text.setOnClickListener(this)
        activity.point_icon.setOnClickListener(this)
    }

    override fun bindViewModel(disposable: CompositeDisposable) {
        disposable.add(PointManager.pointStream.observeOn(AndroidSchedulers.mainThread()).subscribe({
            activity.point_text.text = PointManager.point.toString()
        },{}))
    }

    override fun unbindViewModel() {}

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.point_text, R.id.point_icon -> {
                if (PointManager.point == 0) {
                    PointManager.createFullAd(activity) {
                        it.show()
                    }
                } else {
                    PointManager.createFullAd(activity) {
                        it.show()
                    }
                }
            }
        }
    }
}