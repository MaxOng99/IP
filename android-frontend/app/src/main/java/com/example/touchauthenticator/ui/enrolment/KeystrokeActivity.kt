package com.example.touchauthenticator.ui.enrolment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton

class KeystrokeActivity: EnrolmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.initialiseButtons()
        this.initialiseObservers()
    }

    override fun initialiseObservers() {
        super.initialiseObservers()

        val booleanObserver = androidx.lifecycle.Observer<Boolean> { submitted ->
            if (submitted) {
                viewModel.successStatus.value = false
                ActivityLauncher.launchHomeActivity(this, viewModel.currentUser)
            }
        }
        viewModel.successStatus.observe(this, booleanObserver)
    }

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    override fun initialiseButtons() {
        for (i in 0..8) {
            val id = resources.getIdentifier("btn$i", "id", packageName)
            val button: MaterialButton = findViewById(id)
            buttons.add(button)

            /** Capture raw touch gesture data*/
            button.setOnTouchListener { btn, motionEvent ->

                var index:Int = buttons.indexOf(btn)

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {

                        if (index == currentIndex) {
                            inBound = true
                            viewModel.recordEvent(index, motionEvent)
                        } else {
                            viewModel.resetSample()
                            shakeEffect()
                        }
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (inBound) {
                            viewModel.recordEvent(index, motionEvent)
                            inBound = false
                        }
                        true
                    }
                    else -> super.onTouchEvent(motionEvent)
                }
            }
        }
    }
}