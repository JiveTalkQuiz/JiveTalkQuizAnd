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
import com.bwillocean.jiveQuizTalk.Def
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.data.ResolveRepository
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.dialog.ResponseDialogUtil
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.quizList.QuizListActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.quiz_activity.*
import kotlinx.android.synthetic.main.quiz_list_activity.adView


class QuizUIViews(val layer: ViewGroup, val answerText: TextView, val icon: ImageView)

class QuizActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
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
            quizPassed = ResolveRepository.instance.isResolved(quizItem.title)
        } ?: run {
            finish()
        }

        showAd()
        showQuiz()
        showPoint()
        setupFullAd()

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

    fun setupFullAd() {
        InterstitialAd = InterstitialAd(this@QuizActivity)
        InterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        InterstitialAd.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Log.e("TEST", "onAdFailedToLoad $p0")
            }

            override fun onAdClosed() { // Load the next interstitial.
                PointManager.point += PointManager.AD_POINT
            }
        }
        InterstitialAd.loadAd(AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build())
    }

    fun showPoint() {
        point_text.text = PointManager.point.toString()
        point_icon.setOnClickListener(this)
        point_text.setOnClickListener(this)
    }

    fun showAd() {
        AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build().let { adRequest ->
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
        quiz_main_title.text = quizItem.title
        quiz_main_text.text = quizItem.word.trim()

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
            }
        }
    }

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
                    if(InterstitialAd.isLoaded) {
                        InterstitialAd.show()
                    } else {
                        Log.w("TEST", "ad load fail")
                    }
                } else {
                    PointManager.point -= PointManager.HINT_POINT

                    //TODO hint
                    Toast.makeText(this, "힌트 처리 어케 하지? 딤드 이미지 줘요0", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.statement_1 -> {
                checkCorrectness(view)
                submit(0)
            }
            R.id.statement_2 -> {
                checkCorrectness(view)
                submit(1)
            }
            R.id.statement_3 -> {
                checkCorrectness(view)
                submit(2)
            }
            R.id.statement_4 -> {
                checkCorrectness(view)
                submit(3)
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
            ResponseDialogUtil.responseDialog(this, true, DialogInterface.OnCancelListener {
                ResolveRepository.instance.saveResolved(quizItem.title)
                setResult(Def.ACTIVITY_RESPONSE_CODE_NEXT_QUIZ)
                finish()
            })
        } else {
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