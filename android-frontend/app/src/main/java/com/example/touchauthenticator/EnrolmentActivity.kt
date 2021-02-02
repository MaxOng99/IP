package com.example.touchauthenticator


import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import androidx.gridlayout.widget.GridLayout
import com.example.touchauthenticator.model.TouchGestureData
import com.google.android.material.imageview.ShapeableImageView


import kotlin.random.Random


class EnrolmentActivity : AppCompatActivity(), SensorEventListener{

    private lateinit var btns:ArrayList<ShapeableImageView>
    private lateinit var accelerometerReadings: ArrayList<Triple<Float, Float, Float>>
    private lateinit var rootLayout: GridLayout
    private var currentIndex:Int = -1
    private val tag = "DEBUG"
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        setContentView(R.layout.activity_enrolment)
        rootLayout = findViewById(R.id.gridLayout)
        btns = ArrayList()
        accelerometerReadings = ArrayList()

        initialiseButtons()
        generateStimuli()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialiseButtons() {
        for (i in 0..8) {
            val id = resources.getIdentifier("btn$i", "id", packageName)
            val button: ShapeableImageView= findViewById(id)
            btns.add(findViewById(id))

            /** Capture raw touch gesture data*/
            button.setOnTouchListener { btn, motionEvent ->

                var downTime: Long
                var downPressure: Float
                var downSize: Float
                var downX: Float
                var downY: Float

                var upTime: Long
                var upPressure: Float
                var upSize: Float
                var upX: Float
                var upY: Float


                var index:Int = btns.indexOf(btn)
                var success:Boolean = false
                var touchData = TouchGestureData()

                when (MotionEventCompat.getActionMasked(motionEvent)) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d(tag, "Action was DOWN")
                        if (index == currentIndex) {
                            btn.setBackgroundResource(R.color.buttonBackgroundColor)
                            generateStimuli()
                            success = true
                            downTime = motionEvent.downTime
                            downPressure = motionEvent.pressure
                            downSize = motionEvent.size
                            downX = motionEvent.x
                            downY = motionEvent.y
                        }

                        else {
                            val shake: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)
                            rootLayout.startAnimation(shake)
                        }
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d(tag, "Action was UP")

                        if (success) {
                            upTime = motionEvent.downTime
                            upPressure = motionEvent.pressure
                            upSize = motionEvent.size
                            upX = motionEvent.x
                            upY = motionEvent.y
                        }
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        Log.d(tag, "Action was CANCEL")
                        true
                    }
                    MotionEvent.ACTION_OUTSIDE -> {
                        Log.d(tag, "Movement occurred outside bounds of current screen element")
                        true
                    }

                    else -> super.onTouchEvent(motionEvent)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun generateStimuli() {
        val index = Random.nextInt(9);
        val btn = btns[index]
        val myAnim:Animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        btn.setBackgroundResource(R.color.activatedBgColor);
        currentIndex = index
        btn.startAnimation(myAnim);
    }

    override fun onSensorChanged(p0: SensorEvent) {
        val x = p0.values[0]
        val y = p0.values[1]
        val z = p0.values[2]

        accelerometerReadings.add(Triple(x, y, z))
        Log.d("accelerometer", "$x $y $z")
    }
    
    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
        Log.d("ok", "ok")
    }

}