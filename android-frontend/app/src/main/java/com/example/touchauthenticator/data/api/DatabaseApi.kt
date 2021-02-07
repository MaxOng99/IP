package com.example.touchauthenticator.data.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class DatabaseApi {

    private val _databaseUrl = "https://ta-project-301801-default-rtdb.europe-west1.firebasedatabase.app/"
    private val dbInstance = FirebaseDatabase.getInstance(_databaseUrl)
    private val database = dbInstance.reference

    val transactionSuccess: MutableLiveData<String> = MutableLiveData<String>()

    init {
        transactionSuccess.value = ""
    }

    fun writeDataInBatch(user: FirebaseUser, touchDataRecords: List<TouchGestureData>) {
        database.child(user.uid).push().setValue(touchDataRecords).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                transactionSuccess.postValue("success")
            } else{
                transactionSuccess.postValue("failed")
            }
        }
    }
}