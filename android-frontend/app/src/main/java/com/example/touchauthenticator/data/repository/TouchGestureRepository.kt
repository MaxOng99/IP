package com.example.touchauthenticator.data.repository

import com.example.touchauthenticator.data.api.DatabaseApi
import com.example.touchauthenticator.data.model.TouchGestureData

class TouchGestureRepository(
    private val databaseApi: DatabaseApi
) {

    private fun addRecord(touchData:TouchGestureData) {
        databaseApi.writeNewData(touchData)
    }

    fun addRecordInBatch(batchRecord: List<TouchGestureData>) {
        databaseApi.writeDataInBatch(batchRecord)
    }

}