package com.example.touchauthenticator.ui.enrolment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class ReactionActivity: EnrolmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.initialiseButtons()
        this.initialiseObservers()
        this.generateStimuli()
    }

    override fun initialiseObservers() {
        super.initialiseObservers()

        val booleanObserver = androidx.lifecycle.Observer<Boolean> { submitted ->
            if (submitted) {
                viewModel.successStatus.value = false
                ActivityLauncher.launchKeystrokeActivity(this, viewModel.currentUser)
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
                            btn.backgroundTintList = resources.getColorStateList(R.color.buttonBackgroundColor, null)
                            generateStimuli()
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

    @SuppressLint("ResourceAsColor", "NewApi")
    private fun generateStimuli() {
        val index = Random.nextInt(9);
        val btn = buttons[index]
        val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        btn.backgroundTintList = resources.getColorStateList(R.color.activatedBgColor, null)
        currentIndex = index
        btn.startAnimation(myAnim);
    }
}