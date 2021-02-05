package com.example.touchauthenticator.data.api

import android.util.Log
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.database.FirebaseDatabase


class DatabaseApi {

    private val _databaseUrl = "https://ta-project-301801-default-rtdb.europe-west1.firebasedatabase.app/"
    private val dbInstance = FirebaseDatabase.getInstance(_databaseUrl)
    private val database = dbInstance.reference

    fun writeNewData(touchData:TouchGestureData) {
        val userId = touchData.userId
        val sample = touchData.data
        database.child(userId).push().setValue(sample)
    }

    fun writeDataInBatch(touchDataRecords: List<TouchGestureData>) {
        for (record in touchDataRecords) {
            Log.d("Writing Data:", record.toString())
            this.writeNewData(record)
        }
    }
}