package com.bwillocean.jiveQuizTalk.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.main.view.AdView
import com.bwillocean.jiveQuizTalk.main.view.MainView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    lateinit var mainViewModel: MainViewModel
    lateinit var adView: AdView
    lateinit var mainView: MainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(this))[MainViewModel::class.java]

        adView = AdView(this, mainViewModel)
        mainView = MainView(this, mainViewModel)

        mainViewModel.loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
