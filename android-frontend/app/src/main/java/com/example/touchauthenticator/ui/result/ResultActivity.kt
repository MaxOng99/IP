package com.example.touchauthenticator.ui.result

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text

class ResultActivity: AppCompatActivity() {

    private val viewModel: ResultViewModel = ResultViewModel()
    private var users: ArrayList<TextView> = ArrayList()
    private lateinit var activityLauncherButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentUser = intent.extras?.getParcelable("user")!!
        viewModel.predictionResult = intent.extras?.getSerializable("predictionResults") as HashMap<String, String>
        viewModel.nextActivity = intent?.extras?.getString("nextActivity").toString()
        setContentView(R.layout.prediction_result_ui)
        this.initialiseViews()
    }

    private fun initialiseViews() {
        activityLauncherButton = findViewById(R.id.activityLauncher)

        for (i in 1..4) {
            val id = resources.getIdentifier("user$i", "id", packageName)
            val textView: TextView = findViewById(id)
            val userResult = viewModel.predictionResult["key"]
            textView.text = userResult
            users.add(textView)
        }

        if (viewModel.nextActivity == "keystroke") {
            activityLauncherButton.text = "Keystroke Experiment"
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