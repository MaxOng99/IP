package com.example.touchauthenticator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.R
import com.example.touchauthenticator.ui.enrolment.EnrolmentActivity
import com.google.firebase.auth.FirebaseUser

class HomeActivity: AppCompatActivity() {

    private val viewModel: HomeViewModel = HomeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel.currentUser = intent.extras?.getParcelable<FirebaseUser>("user")!!
    }

    fun launchEnrolmentUI(view: View) {
        val intent = Intent(this, EnrolmentActivity::class.java)
        intent.putExtra("user", viewModel.currentUser)
        startActivity(intent)
    }

    fun launchTestingUI(view: View) {

    }

    fun launchStatisticsUI(view: View) {

    }

}