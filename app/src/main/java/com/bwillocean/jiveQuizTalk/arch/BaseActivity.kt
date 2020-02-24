package com.bwillocean.jiveQuizTalk.arch

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.sound.SoundManager

interface ConfigurationDelegate {
    fun onConfigurationChanged(newConfig: Configuration?)
}

enum class ActivityState {
    CREATED, STARTED, STOPED, DESTROYED
}

open class BaseActivity: AppCompatActivity() {
    companion object {
        val stateMap = mutableMapOf<Int, ActivityState>()
    }

    private val baseViewList = mutableListOf<BaseView>()
    private val handler = Handler()
    private val changeState = Runnable {
        val anyStarted = stateMap.values.any {
            it == ActivityState.STARTED
        }

        if (anyStarted) {
            SoundManager.resumeBg()
        } else {
            SoundManager.pauseBg()
        }
    }


    fun addBaseView(baseView: BaseView) {
        if (!baseViewList.contains(baseView)) {
            baseViewList.add(baseView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateMap[this.hashCode()] = ActivityState.CREATED
    }

    override fun onRestart() {
        super.onRestart()
        stateMap[this.hashCode()] = ActivityState.STARTED

        handler.removeCallbacks(changeState)
        handler.postDelayed(changeState, 1000)
    }

    override fun onStop() {
        super.onStop()
        stateMap[this.hashCode()] = ActivityState.STOPED

        handler.removeCallbacks(changeState)
        handler.postDelayed(changeState, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        stateMap[this.hashCode()] = ActivityState.DESTROYED
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
/*
        try {
            FontChangeCrawler(ResourcesCompat.getFont(this, R.font.hana_pro)!!).run {
                replaceFonts(findViewById(android.R.id.content))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
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

class FontChangeCrawler(private val typeface: Typeface) {
    fun replaceFonts(view: View?) {
        if (view is ViewGroup) {
            val viewTree = view
            var child: View?
            for (i in 0 until viewTree.childCount) {
                child = viewTree.getChildAt(i)
                if (child is ViewGroup) { // recursive call
                    replaceFonts(child)
                } else if (child is TextView) { // base case
                    child.typeface = typeface
                }
            }
        } else if (view is TextView) { // base case
            view.typeface = typeface
        }
    }

}