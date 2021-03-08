package com.example.touchauthenticator.ui.enrolment

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

import kotlin.properties.Delegates

open class EnrolmentViewModel(
    private val touchGestureRepository: TouchGestureRepository
):ViewModel() {


    /**Defines the number of taps to be considered a sample
     * and the number of samples to be collected during enrolment
     */
    var _numberOfTaps = 4
    var _numberOfSamples = 10

    /** Variables that keep track of UI state*/
    private var counter = Counter()
    var completedSamples = MutableLiveData<Int>()
    var currIndex = MutableLiveData<Int>()
    lateinit var enrolmentActivity: String

    /** Variables that keep track of reaction time UI state */
    var reactionArray = IntArray(4)

    private fun initReactionArray() {
        reactionArray[0] = 3
        reactionArray[1] = 2
        reactionArray[2] = 1
        reactionArray[3] = 3

        this.shuffle()
    }

    private fun shuffle() {
        reactionArray.shuffle()
    }

    /** Backend data */
    @SuppressLint("NewApi")
    private var currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY"))
    private var upEvents = ArrayList<TouchGestureData.RawData>()
    private var downEvents = ArrayList<TouchGestureData.RawData>()
    private var touchGestureSamples = ArrayList<TouchGestureData>()
    var successStatus:MutableLiveData<String> = touchGestureRepository.getSuccessStatus()
    lateinit var currentUser: FirebaseUser

    init {
        initReactionArray()
        completedSamples.value = 0
        currIndex.value = this.reactionArray[counter.offset]
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

        var sample = arrayOfNulls<HashMap<String, Number>>(4)
        var duplicateEncountered = false

        for (i in 0 until downEvents.size) {
            var touchData = HashMap<String, Number>()
            val index = downEvents[i].index

            touchData["index"] = index
            touchData["tapCount"] = i

            touchData["downTimestamp"] = downEvents[i].timestamp
            touchData["downPressure"] = downEvents[i].pressure
            touchData["downX"] = downEvents[i].x
            touchData["downY"] = downEvents[i].y

            touchData["upTimestamp"] = upEvents[i].timestamp
            touchData["upPressure"] = upEvents[i].pressure
            touchData["upX"] = upEvents[i].x
            touchData["upY"] = upEvents[i].y

            when (index) {
                2 -> sample[0] = touchData
                1 -> sample[1] = touchData
                3 -> {
                    if (!duplicateEncountered) {
                        sample[2] = touchData
                        duplicateEncountered = true
                    }
                    else{
                        sample[3] = touchData
                    }
                }
            }
        }

        touchGestureSamples.add(TouchGestureData(sample.toList() as List<HashMap<String, Number>>, currentTime, completedSamples.value!!))
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
        var offset: Int by Delegates.observable(0) { _, _, _ ->

            if (offset == 4) {
                resetOffset()
            }

            this@EnrolmentViewModel.currIndex.postValue(reactionArray[offset])
        }

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
            this@EnrolmentViewModel.shuffle()
            offset = 0
        }
    }
}