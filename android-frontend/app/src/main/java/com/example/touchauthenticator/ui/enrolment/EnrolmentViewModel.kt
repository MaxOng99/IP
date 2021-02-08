package com.example.touchauthenticator.ui.enrolment

import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.properties.Delegates

open class EnrolmentViewModel(
    private val touchGestureRepository: TouchGestureRepository
):ViewModel() {


    /**Defines the number of taps to be considered a sample
     * and the number of samples to be collected during enrolment
     */
    var _numberOfTaps = 4
    private var _numberOfSamples = 10


    /** Variables that keep track of UI state*/
    private var counter = Counter()
    var completedSamples = MutableLiveData<Int>()
    lateinit var enrolmentActivity: String

    /** Backend data */
    private var upEvents = ArrayList<TouchGestureData.RawData>()
    private var downEvents = ArrayList<TouchGestureData.RawData>()
    private var touchGestureSamples = ArrayList<TouchGestureData>()
    var successStatus:MutableLiveData<String> = touchGestureRepository.getSuccessStatus()
    lateinit var currentUser: FirebaseUser

    init {
        completedSamples.value = 0
    }

    /**
     * Stores a single tap information into memory. This function
     * is called whenever a user successfully performs a tap.
     */
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
            }
            MotionEvent.ACTION_UP -> {
                upEvents.add(data)
                counter.increment()
            }
        }
    }

    /**
     * Resets the counter of the current sample. This function is called
     * whenever a user tapped out of bounds to ensure the correct number of
     * samples is collected during enrolment.
     */
    fun resetSample() {
        downEvents.clear()
        upEvents.clear()
        counter.resetOffset()
    }

    /**
     * Combines the most recent n tap information into a sample.
     * The variable n indicates the number of taps to be considered
     * a single sample. This new sample will be stored in memory.
     */
    private fun commitSample() {
        val sample = ArrayList<Pair<TouchGestureData.RawData, TouchGestureData.RawData>>()
        for (i in 0 until downEvents.size) {
            sample.add(Pair(downEvents[i], upEvents[i]))
        }
        touchGestureSamples.add(TouchGestureData(sample))
        downEvents.clear()
        upEvents.clear()
    }

    /**
     * Send the recorded touch gesture samples to the repository to be saved permanently.
     */
    private suspend fun uploadData() {
        touchGestureRepository.addRecordInBatch(currentUser, touchGestureSamples, enrolmentActivity)
    }

    /**
     * A class that keeps track of user's progress through the enrolment phase.
     * It records the number of completed samples, as well as resetting its state
     * when users' made an error during enrolment. Whenever a user successfully
     * provide a sample, the view model will be notified to commit the data to memory.
     */
    inner class Counter(
    ) {
        private var offset: Int = 0

        /**
         * Notifies the view model to commit data whenever a sample
         * is successfully submitted by the user.
         */
        private var completedSamples: Int by Delegates.observable(0) { _, _, _ ->

            viewModelScope.launch {
                this@EnrolmentViewModel.completedSamples.postValue( completedSamples)
                commitSample()

                if (completedSamples == _numberOfSamples) {
                    uploadData()
                }
            }
        }

        /**
         * To be called whenever a successful tap occurs.
         */
        fun increment() {
            offset++
            if (offset != 1 && offset % _numberOfTaps == 0) {
                completedSamples++
                resetOffset()
            }
        }

        /**
         * To be called when users' made an error during enrolment.
         */
        fun resetOffset() {
            offset = 0
        }
    }
}