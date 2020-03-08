package com.bwillocean.jiveQuizTalk.sound

import android.media.MediaPlayer
import com.bwillocean.jiveQuizTalk.MyApplication
import com.bwillocean.jiveQuizTalk.R
import com.bwillocean.jiveQuizTalk.data.SolveManager


object SoundManager {
    private fun tempPlay(mp: MediaPlayer) {
        mp.setOnCompletionListener {
            it.stop()
            it.release()
        }
        mp.start()
    }

    fun correctEffect() {
        Thread(Runnable {
            val mp: MediaPlayer = MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.correct)
            tempPlay(mp)
        }).start()

    }
    fun incorrectEffect() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.incorrect)
            tempPlay(mp)
        }).start()
    }

    fun noMoney() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.empty)
            tempPlay(mp)
        }).start()
    }

    fun quizStart() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.start)
            tempPlay(mp)
        }).start()
    }

    var bgMediaPlayer = MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.bg).also {
        it.isLooping = true
    }
    fun playBg() {
        if (SolveManager.getSound()) {
            if (!bgMediaPlayer.isPlaying) {
                bgMediaPlayer.start()
            }
        }
    }
    fun pauseBg() {
        if (bgMediaPlayer.isPlaying) {
            bgMediaPlayer.pause()
        }
    }
}