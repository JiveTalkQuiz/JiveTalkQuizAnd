package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import com.bwillocean.jiveQuizTalk.MyApplication

object SolveManager {
    const val PREF_NAME = "resolver_pref"
    const val KEY_NAME_RESOLVE_PREFIX = "resolver_"
    const val KEY_NAME_HINT_PREFIX = "hint_"
    const val KEY_NAME_ALLOW_SOUND = "sound"
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

    fun checkHint(quizId: Int): Int {
        return preference.getInt(KEY_NAME_HINT_PREFIX+quizId, -1)
    }

    fun setHint(quizId: Int, hintPosition: Int) {
        if (checkHint(quizId) < 0) {
            PointManager.point -= PointManager.HINT_POINT
            preference.edit().putInt(KEY_NAME_HINT_PREFIX+quizId, hintPosition).apply()
        }
    }

    fun setSound(allow: Boolean) {
        preference.edit().putBoolean(KEY_NAME_ALLOW_SOUND, allow).apply()
    }

    fun getSound(): Boolean {
        return preference.getBoolean(KEY_NAME_ALLOW_SOUND, true)
    }

    fun reset() {
        val edit = preference.edit()
        preference.all.keys.forEach {
            edit.remove(it)
        }
        edit.apply()
    }
}
