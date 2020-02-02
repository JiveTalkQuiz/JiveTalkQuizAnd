package com.bwillocean.jiveQuizTalk.arch

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

interface ConfigurationDelegate {
    fun onConfigurationChanged(newConfig: Configuration?)
}

open class BaseActivity: AppCompatActivity() {
    private val baseViewList = mutableListOf<BaseView>()

    fun addBaseView(baseView: BaseView) {
        if (!baseViewList.contains(baseView)) {
            baseViewList.add(baseView)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        delegationConfigurationInterface {
            it.onConfigurationChanged(newConfig)
        }
    }

    private fun delegationConfigurationInterface(each: (ConfigurationDelegate) -> Unit) {
        baseViewList.forEach {
            if (it is ConfigurationDelegate) {
                each(it)
            }
        }
    }
}