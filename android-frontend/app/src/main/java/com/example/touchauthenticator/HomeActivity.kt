package com.example.touchauthenticator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun launchTestingUI(view: View) {

    }

    fun launchEnrolmentUI(view: View) {
        startActivity(Intent(this, EnrolmentActivity::class.java))
    }

    fun launchStatisticsUI(view: View) {

    }

}