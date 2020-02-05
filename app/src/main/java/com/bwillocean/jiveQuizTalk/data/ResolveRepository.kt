package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import com.bwillocean.jiveQuizTalk.MyApplication

class ResolveRepository private constructor(context: Context){
    companion object {
        const val PREF_NAME_RESOLVE = "resolve"
        val instance = ResolveRepository(MyApplication.instance)
    }

    val preference = context.getSharedPreferences(PREF_NAME_RESOLVE, Context.MODE_PRIVATE)

    fun saveResolved(title: String) {
        preference.edit().putBoolean(title, true).apply()
    }

    fun isResolved(title: String): Boolean {
        return preference.getBoolean(title, false)
    }
}