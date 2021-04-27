package com.example.touchauthenticator.ui.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton

class ReactionTestActivity: TestActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.testActivity = "reaction"
        instruction.text = "Tap the highlighted box"
        this.initialiseButtons()
        this.initialiseObservers()
    }

    override fun initialiseObservers() {
        super.initialiseObservers()

        val successStatusObserver = androidx.lifecycle.Observer<String> { dataSentToCloud ->
            if (dataSentToCloud == "success") {
                Toast.makeText(baseContext, "Data sent successfully.",
                    Toast.LENGTH_SHORT).show()
                viewModel.successStatus.value = ""
                ActivityLauncher.launchKeystrokeTestActivity(this, viewModel.currentLegitimateUser)
            }
            else if (dataSentToCloud == "failed"){
                Toast.makeText(baseContext, "Unable to send data. Please check your network connectivity.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        val indexObserver = androidx.lifecycle.Observer<Int> { index ->
            generateStimuli(index)
        }

        viewModel.successStatus.observe(this, successStatusObserver)
        viewModel.currIndex.observe(this, indexObserver)
        viewModel.predictionResponse.observe(this, Observer { response ->

            response.body()?.let {
                ActivityLauncher.launchResultActivity(this, viewModel.currentLegitimateUser,
                    it, "keystroke"
                )
            }
        })
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
                            disableUI()
                        }
                        true
                    }

                    MotionEvent.ACTION_OUTSIDE -> {
                        disableUI()
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
    private fun generateStimuli(index: Int) {

        if (currentIndex != -1) {
            buttons[currentIndex].backgroundTintList = resources.getColorStateList(R.color.buttonBackgroundColor, null)
        }
        //val index = Random.nextInt(9);
        val index = index
        val btn = buttons[index]
        val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        btn.backgroundTintList = resources.getColorStateList(R.color.activatedBgColor, null)
        currentIndex = index
        btn.startAnimation(myAnim);
    }
}