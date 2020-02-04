package com.bwillocean.jiveQuizTalk.point

import android.content.Context
import android.content.SharedPreferences
import com.bwillocean.jiveQuizTalk.MyApplication
import io.reactivex.subjects.PublishSubject
import java.lang.Integer.max
import java.util.prefs.Preferences

object PointManager {
    const val DEFAULT_POINT = 100
    const val FAIL_POINT = 10
    const val HINT_POINT = 10
    const val AD_POINT = 100
    const val PREF_NAME = "point_pref"
    const val KEY_NAME_POINT = "point"
    var point: Int
        get() {
            return MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(KEY_NAME_POINT, 100)
        }
        set(value) {
            pointStream.onNext(point)

            MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_NAME_POINT,
                kotlin.math.max(0, value)
            ).apply()
        }

    val pointStream = PublishSubject.create<Int>()
}