package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import com.bwillocean.jiveQuizTalk.MyApplication
import io.reactivex.subjects.PublishSubject

object ScoreManager {
    const val PREF_NAME = "score_pref"
    const val KEY_NAME_POINT = "score"


    val scoreStream = PublishSubject.create<Int>()
    var score: Int
        get() {
            return MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(
                KEY_NAME_POINT, 0)
        }
        set(value) {
            scoreStream.onNext(
                score
            )

            MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(
                KEY_NAME_POINT,
                kotlin.math.max(0, value)
            ).apply()
        }
}