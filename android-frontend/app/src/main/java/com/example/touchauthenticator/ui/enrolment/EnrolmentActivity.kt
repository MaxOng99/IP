package com.example.touchauthenticator.ui.enrolment


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ServiceLocator
import com.google.android.material.button.MaterialButton
import kotlin.collections.ArrayList


abstract class EnrolmentActivity : AppCompatActivity() {

    /* UI Elements */
    lateinit var gridLayout: GridLayout
    lateinit var dialog:AlertDialog
    lateinit var progress: TextView
    lateinit var instruction: TextView
    lateinit var buttons:ArrayList<MaterialButton>

    /* Variables that track UI status */
    var currentIndex:Int = -1
    var inBound = false

    /* Connecting view model for this class */
    val viewModel: EnrolmentViewModel = EnrolmentViewModel(ServiceLocator.getTouchGestureRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reusable_enroment_ui)
        viewModel.currentUser = intent.extras?.getParcelable("user")!!
        progress = findViewById(R.id.progress)
        gridLayout = findViewById(R.id.gridLayout)
        instruction = findViewById(R.id.instruction)

        buttons = ArrayList()
        createAlertDialog()
    }

    abstract fun initialiseButtons()

    open fun initialiseObservers() {
        val sampleCounterObserver = androidx.lifecycle.Observer<Int> { newCounter ->
            progress.text = "$newCounter/10"
        }
        viewModel.completedSamples.observe(this, sampleCounterObserver)
    }

    private fun enableButtons(state: Boolean) {
        for (button in buttons) {
            button.isClickable = state
            button.isEnabled = state
        }
    }
    fun disableUI() {
        enableButtons(false)
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
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
                enableButtons(true)
            }
        )

        dialog = builder?.create()!!
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

}