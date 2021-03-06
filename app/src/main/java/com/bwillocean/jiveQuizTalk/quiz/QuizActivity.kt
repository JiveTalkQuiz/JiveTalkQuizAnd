package com.bwillocean.jiveQuizTalk.quiz

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bwillocean.jiveQuizTalk.Const
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.dialog.ResponseDialogUtil
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.data.SolveManager
import com.bwillocean.jiveQuizTalk.quizList.QuizListActivity
import com.bwillocean.jiveQuizTalk.sound.SoundManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.quiz_activity.*
import kotlinx.android.synthetic.main.quiz_list_activity.adView
import java.util.*


class QuizUIViews(val layer: ViewGroup, val answerText: TextView, val icon: ImageView)

class QuizActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val TAG = "QuizActivity"
        const val EXTRAS_KEY = "quizItem"
    }

    lateinit var quizItem: QuizItem
    lateinit var InterstitialAd: InterstitialAd
    var quizPassed: Boolean = false

    private var quizChecked = false
    private var disposable: Disposable? = null
    private lateinit var quizUIViews: List<QuizUIViews>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_activity)

        quizUIViews = listOf(
            QuizUIViews(statement_1, statement_1_text, statement_1_check),
            QuizUIViews(statement_2, statement_2_text, statement_2_check),
            QuizUIViews(statement_3, statement_3_text, statement_3_check),
            QuizUIViews(statement_4, statement_4_text, statement_4_check),
            QuizUIViews(statement_5, statement_5_text, statement_5_check)
        )

        (intent.extras?.getSerializable(EXTRAS_KEY) as? QuizItem?)?.let {
            quizItem = it
            quizPassed = SolveManager.checkQuizResult(quizItem.id)
        } ?: run {
            finish()
        }

        showAd()
        showQuiz()
        showPoint()

        prev_icon.setOnClickListener {
            onBackPressed()
        }

        disposable = PointManager.pointStream.observeOn(AndroidSchedulers.mainThread()).subscribe({
            point_text.text = PointManager.point.toString()
        },{})
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun showPoint() {
        point_text.text = PointManager.point.toString()
        point_icon.setOnClickListener(this)
        point_text.setOnClickListener(this)
        hint.setOnClickListener(this)
    }

    fun showAd() {
        AdRequest.Builder().build().let { adRequest ->
            adView.run {
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(code: Int) {
                        super.onAdFailedToLoad(code)
                        Log.e(QuizListActivity.TAG, "ad load error $code")
                    }
                }
                loadAd(adRequest)
            }
        }
    }

    fun showQuiz() {
        quiz_main_title.text = "제${quizItem.id}장"
        quiz_main_text.text = quizItem.word.trim()

        val hintPosition = SolveManager.checkHint(quizId = quizItem.id)

        quizItem.selection.take(5).forEachIndexed { index, quizSelection ->
            quizUIViews[index].layer.visibility = View.VISIBLE

            if (quizPassed && quizSelection.isCorrect) {
                quizUIViews[index].answerText.text = quizSelection.statement
                quizUIViews[index].answerText.setTextColor(Color.WHITE)
                quizUIViews[index].layer.setBackgroundResource(R.drawable.green_rounded_bg)
                quizUIViews[index].icon.visibility = View.VISIBLE
                quizUIViews[index].layer.tag = true
            } else {
                quizUIViews[index].answerText.text = quizSelection.statement
                quizUIViews[index].layer.tag = quizSelection.isCorrect
                quizUIViews[index].layer.setOnClickListener(this)
                quizUIViews[index].answerText.setTextColor(ResourcesCompat.getColor(this@QuizActivity.resources, R.color.greenPointColor, null))
                quizUIViews[index].layer.setBackgroundResource(R.drawable.white_rounded_bg)

                if (hintPosition == index) {
                    quizUIViews[index].layer.alpha = 0.4f
                }
            }
        }
    }

    val rewardAdCallback = PointManager.QuizRewardedAdCallback(this)

    override fun onClick(view: View?) {
        if (quizChecked) {
            return
        }

        if (quizPassed) {
            Toast.makeText(this, "이미 정답을 맞춘 문제 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        when (view?.id) {
            R.id.point_icon, R.id.point_text -> {
                if (PointManager.point == 0) {
                    if (PointManager.rewardAd == null) {
                        PointManager.createRewardAd(this@QuizActivity) { rewardAd ->
                            rewardAd.show(this@QuizActivity, rewardAdCallback)
                        }
                    } else {
                        PointManager.rewardAd?.show(this@QuizActivity, rewardAdCallback)
                        PointManager.rewardAd = null
                    }
                }
            }
            R.id.hint -> {
                if (SolveManager.checkHint(quizItem.id) < 0) {
                    var hintPos = -1
                    do {
                        hintPos = Random(System.currentTimeMillis()).nextInt(quizItem.selection.size)
                    } while(quizItem.selection[hintPos].isCorrect)

                    SolveManager.setHint(quizItem.id, hintPos)
                    showQuiz()
                } else {
                    Toast.makeText(this@QuizActivity, "이미 힌트를 사용하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.statement_1 -> {
                if(SolveManager.checkHint(quizId = quizItem.id) == 0) {
                    return
                }
                checkCorrectness(view)
                submit(0)
            }
            R.id.statement_2 -> {
                if(SolveManager.checkHint(quizId = quizItem.id) == 1) {
                    return
                }
                checkCorrectness(view)
                submit(1)
            }
            R.id.statement_3 -> {
                if(SolveManager.checkHint(quizId = quizItem.id) == 2) {
                    return
                }
                checkCorrectness(view)
                submit(2)
            }
            R.id.statement_4 -> {
                if(SolveManager.checkHint(quizId = quizItem.id) == 3) {
                    return
                }
                checkCorrectness(view)
                submit(3)
            }
            R.id.statement_5 -> {
                if(SolveManager.checkHint(quizId = quizItem.id) == 4) {
                    return
                }
                checkCorrectness(view)
                submit(4)
            }
        }
    }

    private fun submit(index: Int) {
        quizUIViews.forEachIndexed { viewIndex, quizView ->
            if (index == viewIndex) {
                quizView.answerText.setTextColor(Color.WHITE)
                quizView.layer.setBackgroundResource(R.drawable.green_rounded_bg)
                quizView.icon.visibility = View.VISIBLE
            } else {
                quizView.answerText.setTextColor(resources.getColor(R.color.greenPointColor))
                quizView.layer.setBackgroundResource(R.drawable.white_rounded_bg)
                quizView.icon.visibility = View.GONE
            }
        }
    }

    private fun checkCorrectness(view: View) {
        if (view.tag as? Boolean == true) {
            SoundManager.correctEffect()
            ResponseDialogUtil.responseDialog(this, true, DialogInterface.OnCancelListener {
                SolveManager.solveQuiz(quizItem.id)
                setResult(Def.ACTIVITY_RESPONSE_CODE_NEXT_QUIZ)
                finish()
            })
        } else {
            PointManager.point -= PointManager.FAIL_POINT
            SoundManager.incorrectEffect()
            ResponseDialogUtil.responseDialog(this, false, DialogInterface.OnCancelListener {
                finish()
            })
        }
    }

    /**
     * TODO 포인트 클릭 애니메이션 (진동)
     * 포인트 전면 광고 본 후에 다시 볼수 있게 ? 아니라면 문구내용 에러 처리 ? 제한 5분 제한 ?
     * 포인트 0일때 처리 ? (어짜피 4번 누르면 답 볼 수 있는거 아님 ? )
     *
     */
}