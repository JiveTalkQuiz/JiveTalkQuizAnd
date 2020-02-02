package com.bwillocean.jiveQuizTalk.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.bwillocean.jiveQuizTalk.data.model.Quiz
import com.bwillocean.jiveQuizTalk.exception.NoDataException
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Single

class QuizRepository(val context: Context) {
    val storage = FirebaseStorage.getInstance("gs://jivetalk-quiz.appspot.com")

    fun getQuizList(): Single<Quiz> {
        return Single.create<Quiz> { emitter ->
            storage.reference.child("/quiz.json").getBytes(1*1024*1024).addOnFailureListener {
                if(!emitter.isDisposed) emitter.onError(it)
            }.addOnSuccessListener { byte ->
                Klaxon().parse<Quiz>(String(byte, Charsets.UTF_8))?.let {
                    emitter.onSuccess(it)
                } ?: run {
                    if(!emitter.isDisposed) emitter.onError(NoDataException())
                }
            }
        }
    }
}