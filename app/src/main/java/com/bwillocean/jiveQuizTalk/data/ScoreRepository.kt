package com.bwillocean.jiveQuizTalk.data

import com.google.firebase.database.FirebaseDatabase

class ScoreRepository() {
    val reference = FirebaseDatabase.getInstance().getReference("quiz_score")

    fun addScore(name: String, score: Long) {
        reference.setValue(ScoreModel(name, score))
    }
}

data class ScoreModel(val name: String, val score: Long)