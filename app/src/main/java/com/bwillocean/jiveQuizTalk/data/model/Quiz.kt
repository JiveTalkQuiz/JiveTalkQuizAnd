package com.bwillocean.jiveQuizTalk.data.model

import com.beust.klaxon.Json
import java.io.Serializable

data class Quiz(@Json(name = "quiz") val quizList: List<QuizItem>)

data class QuizItem(@Json(name = "title") val title: String, @Json(name = "word") val word: String, @Json(name = "selection") val selection: List<QuizSelection>): Serializable

data class QuizSelection(@Json(name = "statement") val statement: String, @Json(name = "correct") val isCorrect: Boolean): Serializable