package com.example.touchauthenticator.ui.enrolment

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.example.touchauthenticator.data.model.TouchGestureData

class EnrolmentViewModel(

):ViewModel() {

    private var upEvents = ArrayList<TouchGestureData>()
    private var downEvents = ArrayList<TouchGestureData>()

    fun recordEvent(index: Int, event: MotionEvent) {
        val data = TouchGestureData(
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
                Log.d("Tag", data.toString())
            }
        }
    }

    fun addData() {

    }
}