package com.example.touchauthenticator.ui.enrolment


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ServiceLocator
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseUser
import kotlin.collections.ArrayList
import kotlin.random.Random


class EnrolmentActivity : AppCompatActivity() {

    private lateinit var btns:ArrayList<ShapeableImageView>

    private lateinit var gridLayout: GridLayout
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var dialog:AlertDialog
    private lateinit var progress: TextView
    private var currentIndex:Int = -1
    private val viewModel: EnrolmentViewModel = EnrolmentViewModel(ServiceLocator.getTouchGestureRepository())
    private var inBound = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrolment)
        //viewModel.currentUser = intent.extras?.getParcelable<FirebaseUser>("user")!!
        progress = findViewById(R.id.progress)
        gridLayout = findViewById(R.id.gridLayout)
        rootLayout = findViewById(R.id.rootLayout)

        val observer = androidx.lifecycle.Observer<Int> { newCounter ->
            progress.text = "$newCounter/10"
        }

        viewModel.stateCounter.observe(this, observer)

        btns = ArrayList()
        createAlertDialog()
        initialiseLayout()
        initialiseButtons()
        generateStimuli()
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

    @SuppressLint("ResourceAsColor")
    private fun generateStimuli() {
        val index = Random.nextInt(9);
        val btn = btns[index]
        val myAnim:Animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        btn.setBackgroundResource(R.color.activatedBgColor);
        currentIndex = index
        btn.startAnimation(myAnim);
    }
}