package com.bwillocean.jiveQuizTalk.sound

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.rtp.AudioStream
import androidx.core.app.NotificationCompat
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
            mp.setAudioAttributes(AudioAttributes.Builder().apply {
                this.setLegacyStreamType(AudioManager.STREAM_ALARM)
            }.build())
            tempPlay(mp)
        }).start()

    }
    fun incorrectEffect() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.incorrect)
            mp.setAudioAttributes(AudioAttributes.Builder().apply {
                this.setLegacyStreamType(AudioManager.STREAM_ALARM)
            }.build())
            tempPlay(mp)
        }).start()
    }

    fun noMoney() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.empty)
            mp.setAudioAttributes(AudioAttributes.Builder().apply {
                this.setLegacyStreamType(AudioManager.STREAM_ALARM)
            }.build())
            tempPlay(mp)
        }).start()
    }

    fun quizStart() {
        Thread(Runnable {
            val mp: MediaPlayer =
                MediaPlayer.create(MyApplication.instance.applicationContext, R.raw.start)
            mp.setAudioAttributes(AudioAttributes.Builder().apply {
                this.setLegacyStreamType(AudioManager.STREAM_ALARM)
            }.build())
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