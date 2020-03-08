package com.bwillocean.jiveQuizTalk.quizList.view

import android.util.Log
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
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
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

    val rewardAdCallback = PointManager.QuizRewardedAdCallback(activity)
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.point_text, R.id.point_icon, R.id.toolbar_point_icon, R.id.toolbar_point_text -> {
                if (PointManager.point == 0) {
                    if (PointManager.rewardAd == null) {
                        PointManager.createRewardAd(activity) { rewardAd ->
                            rewardAd.show(activity, rewardAdCallback)
                        }
                    } else {
                        PointManager.rewardAd?.show(activity, rewardAdCallback)
                        PointManager.rewardAd = null
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
        ScoreManager.pointLeveLString().let { (descTitle, gradeTitle) ->
            activity.score_desc_title.text = descTitle
            activity.score_grade_title.text = gradeTitle
        }
    }
}