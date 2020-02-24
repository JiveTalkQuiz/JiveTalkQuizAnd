package com.bwillocean.jiveQuizTalk.sound

import android.media.MediaPlayer
import com.bwillocean.jiveQuizTalk.MyApplication
import com.bwillocean.jiveQuizTalk.R


object SoundManager {

    fun correctEffect() {
        Thread(Runnable {
            val mp: MediaPlayer = MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.correct)
            mp.start()
        }).start()

    }
    fun incorrectEffect() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.incorrect)
            mp.start()
        }).start()
    }

    fun noMoney() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.empty)
            mp.start()
        }).start()
    }

    fun quizStart() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.start)
            mp.start()
        }).start()
    }

    val bgMediaPlayer = MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.bg)
    fun playBg() {
        if (!bgMediaPlayer.isPlaying) {
            bgMediaPlayer.isLooping = true
            Thread(Runnable {
                bgMediaPlayer.start()
            }).also{
                it.isDaemon = true
            }.start()
        }
    }
}