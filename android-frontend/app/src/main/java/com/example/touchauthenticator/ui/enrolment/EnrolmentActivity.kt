package com.example.touchauthenticator.ui.enrolment


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.example.touchauthenticator.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseUser
import kotlin.random.Random


class EnrolmentActivity : AppCompatActivity(), SensorEventListener{

    private lateinit var btns:ArrayList<ShapeableImageView>

    private lateinit var gridLayout: GridLayout
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var dialog:AlertDialog
    private var currentIndex:Int = -1
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var accelerometerReadings: ArrayList<Triple<Float, Float, Float>>
    private val viewModel: EnrolmentViewModel by viewModels()
    private var counter:Int = 0
    private var inBound = false

    private fun updateCounter() {

        if (counter < 50) {
            counter++
        }
        else{
            counter = 0
            viewModel.addData()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrolment)
        viewModel.currentUser = intent.extras?.getParcelable<FirebaseUser>("user")!!

        gridLayout = findViewById(R.id.gridLayout)
        rootLayout = findViewById(R.id.rootLayout)
        btns = ArrayList()
        createAlertDialog()
        initialiseLayout()
        initialiseButtons()
        generateStimuli()


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometerReadings = ArrayList()

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
    private fun initialiseLayout() {
        gridLayout.setOnTouchListener { view, motionEvent ->
            shakeEffect()
            true
        }

        rootLayout.setOnTouchListener { view, motionEvent ->
            shakeEffect()
            true
        }
    }

    private fun shakeEffect() {
        val shake: Animation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.shake
        )
        gridLayout.startAnimation(shake)

        dialog.show()
    }

    private fun createAlertDialog() {
        val builder: AlertDialog.Builder? = this?.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage("Please tap within the boxes")
            ?.setTitle("Tapped out of bounds")

        builder?.setPositiveButton(
            "OK",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        dialog = builder?.create()!!
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialiseButtons() {
        for (i in 0..8) {
            val id = resources.getIdentifier("btn$i", "id", packageName)
            val button: ShapeableImageView= findViewById(id)
            btns.add(findViewById(id))

            /** Capture raw touch gesture data*/
            button.setOnTouchListener { btn, motionEvent ->

                var index:Int = btns.indexOf(btn)

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (index == currentIndex) {
                            inBound = true
                            viewModel.recordEvent(index, motionEvent)
                            btn.setBackgroundResource(R.color.buttonBackgroundColor)
                            generateStimuli()
                        } else {
                            shakeEffect()
                        }
                        true
                    }

                    MotionEvent.ACTION_UP -> {

                        if (inBound) {
                            viewModel.recordEvent(index, motionEvent)
                            inBound = false
                            updateCounter()
                        }
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
        //Log.d("accelerometer", "$x $y $z")
    }
    
    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
        //Log.d("ok", "ok")
    }

}