package com.example.touchauthenticator.ui.test


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.example.touchauthenticator.utility.ServiceLocator
import com.google.android.material.button.MaterialButton
import kotlin.collections.ArrayList


abstract class TestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    /* UI Elements */
    lateinit var gridLayout: GridLayout
    lateinit var dialog:AlertDialog
    lateinit var progress: TextView
    lateinit var instruction: TextView
    lateinit var buttons:ArrayList<MaterialButton>
    lateinit var spinner: Spinner
    lateinit var userTextView: TextView

    /* Variables that track UI status */
    var currentIndex:Int = -1
    var inBound = false

    /* Connecting view model for this class */
    val viewModel: TestViewModel = TestViewModel(ServiceLocator.getTouchGestureRepository(), ServiceLocator.getTouchDynamicsApi())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reusable_test_ui)
        viewModel.currentLegitimateUser = intent.extras?.getParcelable("user")!!
        progress = findViewById(R.id.progress)
        gridLayout = findViewById(R.id.gridLayout)
        instruction = findViewById(R.id.instruction)
        userTextView = findViewById(R.id.userTextView)

        spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.users_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this
        buttons = ArrayList()
        createAlertDialog()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val currentUser:String = parent.getItemAtPosition(pos).toString()

        if (viewModel.checkUserComplete(pos+1)) {
            enableButtons(false)
            progress.text = "10/10"
        }
        else{
            enableButtons(true)
            viewModel.resetSample()
            viewModel.resetCompletedSamples()
        }
        userTextView.text = currentUser
        viewModel.currentTestUser = pos+1

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    abstract fun initialiseButtons()

    open fun initialiseObservers() {
        val sampleCounterObserver = androidx.lifecycle.Observer<Int> { newCounter ->
            progress.text = "$newCounter/10"
            if (newCounter == viewModel._numberOfSamples) {
                enableButtons(false)
            }
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