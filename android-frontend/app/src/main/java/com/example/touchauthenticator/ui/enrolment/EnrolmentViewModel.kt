package com.example.touchauthenticator.ui.enrolment

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser

class EnrolmentViewModel(
    private val touchGestureRepository: TouchGestureRepository
):ViewModel() {

    private var _numberOfTaps = 4
    private var _numberOfSamples = 10
    private var tempUp = ArrayList<TouchGestureData.RawData>()
    private var tempDown = ArrayList<TouchGestureData.RawData>()
    private var upEvents = ArrayList<TouchGestureData.RawData>()
    private var downEvents = ArrayList<TouchGestureData.RawData>()
    private var rowData = ArrayList<TouchGestureData>()
    val stateCounter = MutableLiveData<Int>()
    private var internalStateCounter = 0
    lateinit var currentUser: FirebaseUser

    init {
        stateCounter.value = 0
    }

    fun resetSample() {
        tempUp.clear()
        tempDown.clear()
        internalStateCounter = 0
    }

    private fun commitSample(downSamples: ArrayList<TouchGestureData.RawData>, upSamples:ArrayList<TouchGestureData.RawData>) {
        downEvents.addAll(downSamples)
        upEvents.addAll(upSamples)
    }

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
                tempDown.add(data)
                Log.d("Tag", data.toString())
            }
            MotionEvent.ACTION_UP -> {
                tempUp.add(data)
                internalStateCounter++
                val counterPlaceholder = stateCounter.value
                Log.d("Tag", data.toString())

                if (counterPlaceholder != null) {
                    if (internalStateCounter != 1 && internalStateCounter % _numberOfTaps == 0) {
                        this.commitSample(tempDown, tempUp)
                        internalStateCounter = 0
                        stateCounter.value = stateCounter.value?.plus(1)
                    }
                }

                if (stateCounter.value == _numberOfSamples) {
                    submitData()
                    stateCounter.value = 0
                }
            }
        }
    }
}