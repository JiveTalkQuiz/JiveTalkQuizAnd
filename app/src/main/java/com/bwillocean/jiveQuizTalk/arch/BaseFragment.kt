package com.bwillocean.jiveQuizTalk.arch

import androidx.fragment.app.Fragment

class BaseFragment: Fragment() {
    val baseViewList = mutableListOf<BaseView>()

    /**
     * 중복 제거
     */
    fun addBaseView(baseView: BaseView) {
        if (!baseViewList.contains(baseView)) {
            baseViewList.add(baseView)
        }
    }
}