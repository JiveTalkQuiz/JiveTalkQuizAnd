package com.bwillocean.jiveQuizTalk.point

import android.content.Context
import android.content.SharedPreferences
import com.bwillocean.jiveQuizTalk.MyApplication
import java.util.prefs.Preferences

object PointManager {
    const val DEFAULT_POINT = 100
    const val PREF_NAME = "point_pref"
    const val KEY_NAME_POINT = "point"
    var point: Int
        get() = MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(KEY_NAME_POINT, 100)
        set(value) = MyApplication.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_NAME_POINT, value).apply()
}