package com.bwillocean.jiveQuizTalk.quizList.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.data.model.QuizItem
import kotlinx.android.synthetic.main.quiz_item.view.*

class QuizViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val text: TextView = view.item
}
class QuizAdapter: RecyclerView.Adapter<QuizViewHolder>() {
    private var list = listOf<QuizItem>()

    fun setQuiz(quiz: Quiz) {
        list = quiz.quizList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): QuizItem? {
        return list[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        return QuizViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.quiz_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.text.text = position.toString()
    }
}