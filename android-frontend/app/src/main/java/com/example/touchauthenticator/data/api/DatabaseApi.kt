package com.example.touchauthenticator.data.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
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

    fun writeDataInBatch(user: FirebaseUser, touchDataRecords: List<TouchGestureData>, activity:String) {

        var userId = user.uid
        var updatedUserData = HashMap<String, Any>()

        for (record in touchDataRecords) {
            var newRecord = database.child(activity).child(userId).push()
            var newRecordKey = newRecord.key
            updatedUserData["$activity/$userId/$newRecordKey"] = record
        }

        database.updateChildren(updatedUserData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                transactionSuccess.postValue("success")
            }
            else {
                transactionSuccess.postValue("failed")
            }
        }
    }
}