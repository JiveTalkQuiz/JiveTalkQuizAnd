package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import com.bwillocean.jiveQuizTalk.MyApplication

object SolveManager {
    const val PREF_NAME = "resolver_pref"
    const val KEY_NAME_RESOLVE_PREFIX = "resolver_"
    private val preference = MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun checkQuizResult(quizId: Int): Boolean {
        return preference.getBoolean(KEY_NAME_RESOLVE_PREFIX+quizId, false)
    }

    fun solveQuiz(quizId: Int) {
        if (!checkQuizResult(quizId)) {
            ScoreManager.score += 1
            preference.edit().putBoolean(KEY_NAME_RESOLVE_PREFIX+quizId, true).apply()
        }
    }
}
