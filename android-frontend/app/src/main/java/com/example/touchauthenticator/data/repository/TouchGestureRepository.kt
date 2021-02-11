package com.example.touchauthenticator.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.touchauthenticator.data.api.DatabaseApi
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TouchGestureRepository(
    private val databaseApi: DatabaseApi
){

    private val transactionSuccess: MutableLiveData<String> = databaseApi.transactionSuccess

    fun getSuccessStatus(): MutableLiveData<String> {
        return transactionSuccess
    }

    suspend fun addRecordInBatch(user: FirebaseUser, batchRecord: List<TouchGestureData>, activity:String) {
        withContext(Dispatchers.IO) {
            databaseApi.writeDataInBatch(user, batchRecord, activity)
        }
    }
}