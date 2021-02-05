package com.example.touchauthenticator.ui.enrolment

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser

class EnrolmentViewModel(
    private val touchGestureRepository: TouchGestureRepository
):ViewModel() {

    private var _numberOfTaps = 5
    private var _numberOfSamples = 1
    private var upEvents = ArrayList<TouchGestureData.RawData>()
    private var downEvents = ArrayList<TouchGestureData.RawData>()
    private var rowData = ArrayList<TouchGestureData>()
    private var stateCounter = 0
    lateinit var currentUser: FirebaseUser

    private fun submitData() {
        var temp = ArrayList<Pair<TouchGestureData.RawData, TouchGestureData.RawData>>()
        for (i in 0 until downEvents.size) {
            val downRawData = downEvents[i]
            val upRawData = upEvents[i]
            temp.add(Pair(downRawData, upRawData))
            if (i != 0 && i != 1 && (i+1) % _numberOfTaps == 0) {
                rowData.add(TouchGestureData(currentUser.uid, temp))
            }
        }
        touchGestureRepository.addRecordInBatch(rowData)
    }

    fun recordEvent(index: Int, event: MotionEvent) {
        val data = TouchGestureData.RawData(
            index,
            event.pressure,
            event.eventTime,
            event.x,
            event.y
        )

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downEvents.add(data)
                Log.d("Tag", data.toString())
            }
            MotionEvent.ACTION_UP -> {
                upEvents.add(data)
                stateCounter++
                Log.d("Tag", data.toString())

                if (stateCounter == _numberOfTaps * _numberOfSamples) {
                    submitData()
                    stateCounter = 0
                }
            }
        }
    }

}