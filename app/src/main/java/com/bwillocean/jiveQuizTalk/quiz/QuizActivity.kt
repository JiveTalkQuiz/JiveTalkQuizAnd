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
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import com.bwillocean.jiveQuizTalk.dialog.ResponseDialogUtil
import com.bwillocean.jiveQuizTalk.point.PointManager
import com.bwillocean.jiveQuizTalk.quizList.QuizListActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.quiz_activity.*
import kotlinx.android.synthetic.main.quiz_list_activity.adView

class QuizActivity: AppCompatActivity(), View.OnClickListener {
    companion object {
        const val EXTRAS_KEY ="quizItem"
    }

    lateinit var quizItem: QuizItem

    private var quizChecked = false
    private lateinit var quizStateLayoutList: List<ViewGroup>
    private lateinit var quizStateViewList: List<TextView>
    private lateinit var quizStateCheckIconList: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_activity)

        quizStateLayoutList = listOf(statement_1, statement_2, statement_3, statement_4)
        quizStateViewList = listOf(statement_1_text, statement_2_text, statement_3_text, statement_4_text)
        quizStateCheckIconList = listOf(statement_1_check, statement_2_check, statement_3_check, statement_4_check)

        (intent.extras?.getSerializable(EXTRAS_KEY) as? QuizItem?)?.let {
            quizItem = it
        } ?: run {
            finish()
        }

        showAd()
        showQuiz()
        showPoint()

        prev_icon.setOnClickListener {
            onBackPressed()
        }
    }

    fun showPoint() {
        point_text.text = PointManager.point.toString()
        point_icon.setOnClickListener {
            //포인트 사용.
            //힌트 노출
        }
    }

    fun showAd() {
        AdRequest.Builder().build().let { adRequest ->
            adView.run {
                adListener = object: AdListener() {
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

        quizItem.selection.take(4).forEachIndexed { index, quizSelection ->
            quizStateViewList[index].text = quizSelection.statement
            quizStateLayoutList[index].tag = quizSelection.isCorrect
            quizStateLayoutList[index].setOnClickListener(this)
        }
    }

    override fun onClick(view: View?) {
        if (quizChecked) {
            return
        }

        when(view?.id) {
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

        quizChecked = true
    }

    private fun submit(index: Int) {
        quizStateCheckIconList[index].visibility = View.VISIBLE
        quizStateViewList[index].setTextColor(Color.WHITE)
        quizStateLayoutList[index].setBackgroundColor(Color.CYAN)
    }

    private fun checkCorrectness(view: View) {
        if (view.tag as? Boolean == true) {
            ResponseDialogUtil.responseDialog(this, true, DialogInterface.OnCancelListener {
                //finish()
            })
        } else {
            ResponseDialogUtil.responseDialog(this, false, DialogInterface.OnCancelListener {
                //finish()
            })
        }
    }
}