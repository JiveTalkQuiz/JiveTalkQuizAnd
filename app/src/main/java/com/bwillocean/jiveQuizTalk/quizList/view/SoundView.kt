package com.bwillocean.jiveQuizTalk.quizList.view

import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.arch.BaseActivity
import com.bwillocean.jiveQuizTalk.arch.BaseView
import com.bwillocean.jiveQuizTalk.data.SolveManager
import com.bwillocean.jiveQuizTalk.quizList.MainViewModel
import com.bwillocean.jiveQuizTalk.sound.SoundManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quiz_list_activity.*

class SoundView(private val activity: BaseActivity, val viewModel: MainViewModel): BaseView(activity) {
    val soundIcon = activity.sound_icon

    override fun bindViewModel(disposable: CompositeDisposable) {
        update()
        soundIcon.setOnClickListener {
            SolveManager.setSound(!SolveManager.getSound())
            update()
        }
    }

    override fun unbindViewModel() {

    }

    private fun update() {
        if (SolveManager.getSound()) {
            SoundManager.playBg()
            soundIcon.setImageResource(R.drawable.ico_sound_on)
        } else {
            SoundManager.pauseBg()
            soundIcon.setImageResource(R.drawable.ico_sound_off)
        }
    }
}