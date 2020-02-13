package com.bwillocean.jiveQuizTalk.quizList.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.data.PointManager
import com.bwillocean.jiveQuizTalk.data.SolveManager
import com.bwillocean.jiveQuizTalk.data.ScoreManager
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import kotlinx.android.synthetic.main.quiz_item.view.*
import kotlinx.android.synthetic.main.quiz_list_header.view.*
import kotlin.math.max

class QuizViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val text: TextView = view.item
}
class QuizHeaderHolder(view: View): RecyclerView.ViewHolder(view) {
    val pointTitle = view.point_text
    val scoreTitle = view.score_desc_title
    val scoreGradeTitle = view.score_grade_title
}

class QuizAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_HEADER = 1

        private const val TYPE_COUNT = 1

        fun relativePosition(position: Int): Int {
            return max(0, position-(TYPE_COUNT-1))
        }
    }

    var clickListener: View.OnClickListener? = null

    private var list = listOf<QuizItem>()

    fun setQuiz(quiz: Quiz) {
        list = quiz.quizList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): QuizItem? {
        return list[relativePosition(position)]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            LayoutInflater.from(parent.context).inflate(R.layout.quiz_list_header, parent, false).let {
                QuizHeaderHolder(it)
            }
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.quiz_item, parent, false).also {
                it.setOnClickListener {
                    clickListener?.onClick(it)
                }
            }.let {
                QuizViewHolder(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.count() + (TYPE_COUNT-1)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuizHeaderHolder) {
            holder.pointTitle.text = PointManager.point.toString()

            when(ScoreManager.score) {
                in 0..20 -> {
                    holder.scoreGradeTitle.text = "라떼는 말이야~"
                    holder.scoreTitle.text = "Lv. 아재"
                }
                in 21..40 -> {
                    holder.scoreGradeTitle.text = "아 어려워 급식체 거부!"
                    holder.scoreTitle.text = "Lv. 문찐"
                }
                in 41..60 -> {
                    holder.scoreGradeTitle.text = "이걸 또 줄여? 하 불편해"
                    holder.scoreTitle.text = "Lv. 프로불편러"
                }
                in 61..80 -> {
                    holder.scoreGradeTitle.text = "오~ 당신은 놀줄아는 놈"
                    holder.scoreTitle.text = "Lv. 오 놀아봄"
                }
                in 81..100-> {
                    holder.scoreGradeTitle.text = "이런다고 인싸 안되는거 아는 당신은"
                    holder.scoreTitle.text = "Lv. 인싸"
                }
                else -> {
                    holder.scoreGradeTitle.text = "존경합니다."
                    holder.scoreTitle.text = "Lv. 신"
                }
            }
        } else if (holder is QuizViewHolder){
            holder.itemView.tag = list[relativePosition(position)]
            if (SolveManager.checkQuizResult(list[relativePosition(position)].id)) {
                holder.text.setBackgroundResource(R.drawable.stage_check)
                holder.text.text = ""
            } else {
                holder.text.setBackgroundResource(R.drawable.stage)
                holder.text.text = list[relativePosition(position)].id.toString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        /*return if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }*/
        return TYPE_ITEM
    }
}