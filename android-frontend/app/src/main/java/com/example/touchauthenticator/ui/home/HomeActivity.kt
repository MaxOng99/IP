package com.example.touchauthenticator.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.google.firebase.auth.FirebaseUser

class HomeActivity: AppCompatActivity() {

    private val viewModel: HomeViewModel = HomeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel.currentUser = intent.extras?.getParcelable<FirebaseUser>("user")!!
    }

    fun launchEnrolmentUI(view: View) {
        ActivityLauncher.launchReactionActivity(this, viewModel.currentUser)
    }

    fun launchTestingUI(view: View) {
        ActivityLauncher.launchTestingActivity()
    }

    fun launchStatisticsUI(view: View) {
        ActivityLauncher.launchTestingActivity()
    }

}