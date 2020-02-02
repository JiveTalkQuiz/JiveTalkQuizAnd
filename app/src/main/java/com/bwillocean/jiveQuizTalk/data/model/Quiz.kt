package com.bwillocean.jiveQuizTalk.data.model

import com.beust.klaxon.Json

data class Quiz(@Json(name = "quiz") val quizList: List<QuizItem>)

data class QuizItem(@Json(name = "word") val word: String, @Json(name = "selection") val selection: List<QuizSelection>)

data class QuizSelection(@Json(name = "statement") val statement: String, @Json(name = "correct") val isCorrect: Boolean)