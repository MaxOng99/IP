package com.example.touchauthenticator.data.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DatabaseApi {

    private val _databaseUrl = "https://ta-project-301801-default-rtdb.europe-west1.firebasedatabase.app/"
    private val dbInstance = FirebaseDatabase.getInstance(_databaseUrl)
    private val database = dbInstance.reference

    val transactionSuccess: MutableLiveData<String> = MutableLiveData<String>()

    init {
        transactionSuccess.value = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeDataInBatch(user: FirebaseUser, touchDataRecords: List<TouchGestureData>, activity:String) {
        val currentTimestamp = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MM YYYY")
        val formatted = currentTimestamp.format(formatter)
        database.child(activity).child(user.uid).child(formatted).setValue(touchDataRecords).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                transactionSuccess.postValue("success")
            } else{
                transactionSuccess.postValue("failed")
            }
        }
    }
}