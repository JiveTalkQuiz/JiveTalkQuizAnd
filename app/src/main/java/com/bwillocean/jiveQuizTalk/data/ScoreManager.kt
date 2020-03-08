package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import android.widget.Toast
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
                value
            )

            if (scoreLevel(value) != scoreLevel(score)) {
                PointManager.point += PointManager.LEVEL_UP
                Toast.makeText(MyApplication.instance, "레벨업", Toast.LENGTH_SHORT).show()
            }

            MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(
                KEY_NAME_POINT,
                kotlin.math.max(0, value)
            ).apply()
        }

    fun scoreLevel(newScore: Int): Int {
        return when(newScore) {
            in 0..5 -> 0
            in 6..40 -> 1
            in 41..60 -> 2
            in 61..80 -> 3
            in 81..100-> 4
            else -> 100
        }
    }

    fun pointLeveLString(): Pair<String, String> {
        return when(scoreLevel(score)) {
            0 -> Pair("라떼는 말이야~", "Lv. 아재")
            1 -> Pair("아 어려워 급식체 거부!", "Lv. 문찐")
            2 -> Pair("이걸 또 줄여? 하 불편해", "Lv. 프로불편러")
            3 -> Pair("오~ 당신은 놀줄아는 놈", "Lv. 오 놀아봄")
            4-> Pair("이런다고 인싸 안되는거 아는 당신은", "Lv. 인싸")
            100 -> Pair("존경합니다.", "Lv. 신")
            else -> Pair("", "")
        }
    }
}