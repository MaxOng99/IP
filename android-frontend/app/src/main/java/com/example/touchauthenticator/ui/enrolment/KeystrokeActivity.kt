package com.example.touchauthenticator.ui.enrolment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton

class KeystrokeActivity: EnrolmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.enrolmentActivity = "keystroke"
        instruction.text = "Tap 3244 ten times"
        this.initialiseButtons()
        this.initialiseObservers()
    }

    override fun initialiseObservers() {
        super.initialiseObservers()

        val observer = androidx.lifecycle.Observer<String> { submitted ->
            if (submitted == "success") {
                Toast.makeText(baseContext, "Data sent successfully.",
                    Toast.LENGTH_SHORT).show()
                viewModel.successStatus.value = ""
                ActivityLauncher.launchHomeActivity(this, viewModel.currentUser)
            }else if (submitted == "failed"){
                Toast.makeText(baseContext, "Unable to send data. Please check your network connectivity.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.successStatus.observe(this, observer)
    }

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    override fun initialiseButtons() {
        for (i in 0..8) {
            val id = resources.getIdentifier("btn$i", "id", packageName)
            val button: MaterialButton = findViewById(id)
            button.text = (i+1).toString()
            buttons.add(button)

            /** Capture raw touch gesture data*/
            button.setOnTouchListener { btn, motionEvent ->

                var index:Int = buttons.indexOf(btn)

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {

                        inBound = true
                        viewModel.recordEvent(index, motionEvent)
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        btn.backgroundTintList = resources.getColorStateList(R.color.activatedBgColor, null)
                        true
                    }

                    MotionEvent.ACTION_OUTSIDE -> {
                        shakeEffect()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (inBound) {
                            viewModel.recordEvent(index, motionEvent)
                            inBound = false
                            btn.backgroundTintList = resources.getColorStateList(R.color.buttonBackgroundColor, null)
                        }
                        true
                    }
                    else -> super.onTouchEvent(motionEvent)
                }
            }
        }
    }
}