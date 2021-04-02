package com.example.touchauthenticator.ui.result

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.R
import com.example.touchauthenticator.data.model.ResponseWrapper
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import org.w3c.dom.Text

class ResultActivity: AppCompatActivity() {

    private val viewModel: ResultViewModel = ResultViewModel()
    private var users: ArrayList<TextView> = ArrayList()
    private lateinit var activityLauncherButton: Button
    private lateinit var metricsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentUser = intent.extras?.getParcelable("user")!!
        val serialisedResponse = intent.extras?.getString("predictionResults");
        viewModel.predictionResult = Gson().fromJson(serialisedResponse, ResponseWrapper::class.java);
        viewModel.authResult = intent.extras?.getString("authResult").toString()
        viewModel.nextActivity = intent?.extras?.getString("nextActivity").toString()
        setContentView(R.layout.prediction_result_ui)
        this.initialiseViews()
    }

    private fun initialiseViews() {

        metricsTextView = findViewById(R.id.metrics)
        val eer = "%.3f".format(viewModel.predictionResult.eer)
        val frr = "%.3f".format(viewModel.predictionResult.frr)
        val far = "%.3f".format(viewModel.predictionResult.far)
        metricsTextView.text = "EER: $eer% | FRR: $frr% | FAR: $far%"

        activityLauncherButton = findViewById(R.id.activityLauncher)
        for (i in 1..4) {
            val id = resources.getIdentifier("user$i", "id", packageName)
            val textView: TextView = findViewById(id)
            val userResult = viewModel.predictionResult.predictions[i]
            Log.d("Debug", "$userResult")
            textView.text = "User $i: $userResult/10"
            users.add(textView)
        }

        if (viewModel.nextActivity == "keystroke") {
            activityLauncherButton.text = "To Keystroke Experiment"
        }
        else{
         activityLauncherButton.text = "Exit"
        }
    }

    fun launchNextActivity(view: View) {

        if (viewModel.nextActivity == "keystroke") {
            ActivityLauncher.launchKeystrokeTestActivity(this, viewModel.currentUser)
        }
        else {
            finish()
        }
    }
}