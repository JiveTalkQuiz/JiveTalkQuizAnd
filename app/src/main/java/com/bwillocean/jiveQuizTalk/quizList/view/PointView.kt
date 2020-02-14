package com.bwillocean.jiveQuizTalk.quizList.view

import android.view.View
import android.widget.Toast
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.data.ScoreManager
import com.bwillocean.jiveQuizTalk.data.SolveManager
import com.bwillocean.jiveQuizTalk.dialog.LoadingDialog
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_activity.point_icon
import kotlinx.android.synthetic.main.quiz_activity.point_text
import kotlinx.android.synthetic.main.quiz_list_activity.*

class PointView (val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity),
    View.OnClickListener {

    override fun onCreate() {
        super.onCreate()

        activity.point_text.setOnClickListener(this)
        activity.point_icon.setOnClickListener(this)
        activity.toolbar_point_text.setOnClickListener(this)
        activity.toolbar_point_icon.setOnClickListener(this)
        activity.reset.setOnClickListener(this)

        updatePoint()
        updateScoreGrade()
    }

    override fun bindViewModel(disposable: CompositeDisposable) {
        disposable.add(PointManager.pointStream.observeOn(AndroidSchedulers.mainThread()).subscribe({
            updatePoint()
        },{
            it.printStackTrace()
        }))

        disposable.add(ScoreManager.scoreStream.observeOn(AndroidSchedulers.mainThread()).subscribe({
           updateScoreGrade()
        }, {
           it.printStackTrace()
        }))
    }

    override fun unbindViewModel() {}

    override fun onStart() {
        super.onStart()
        updateScoreGrade()
        updatePoint()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.point_text, R.id.point_icon, R.id.toolbar_point_icon, R.id.toolbar_point_text -> {
                if (PointManager.point == 0) {
                    PointManager.createFullAd(activity) {
                        it.show()
                    }
                }
            }
            R.id.reset -> {
                SolveManager.reset()
                ScoreManager.score = 0
                PointManager.point = 0

                Toast.makeText(activity, "초기화됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePoint() {
        activity.point_text.text = PointManager.point.toString()
        activity.toolbar_point_text.text = PointManager.point.toString()
    }

    private fun updateScoreGrade() {
        when(ScoreManager.score) {
            in 0..20 -> {
                activity.score_desc_title.text = "라떼는 말이야~"
                activity.score_grade_title.text = "Lv. 아재"
            }
            in 21..40 -> {
                activity.score_desc_title.text = "아 어려워 급식체 거부!"
                activity.score_grade_title.text = "Lv. 문찐"
            }
            in 41..60 -> {
                activity.score_desc_title.text = "이걸 또 줄여? 하 불편해"
                activity.score_grade_title.text = "Lv. 프로불편러"
            }
            in 61..80 -> {
                activity.score_desc_title.text = "오~ 당신은 놀줄아는 놈"
                activity.score_grade_title.text = "Lv. 오 놀아봄"
            }
            in 81..100-> {
                activity.score_desc_title.text = "이런다고 인싸 안되는거 아는 당신은"
                activity.score_grade_title.text = "Lv. 인싸"
            }
            else -> {
                activity.score_desc_title.text = "존경합니다."
                activity.score_grade_title.text = "Lv. 신"
            }
        }
    }
}