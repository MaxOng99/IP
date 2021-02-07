package com.example.touchauthenticator.utility

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.ui.enrolment.KeystrokeActivity
import com.example.touchauthenticator.ui.enrolment.ReactionActivity
import com.example.touchauthenticator.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseUser

object ActivityLauncher {


    fun launchKeystrokeActivity(activity: AppCompatActivity, currentUser:FirebaseUser) {
        val intent = Intent(activity, KeystrokeActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
    }

    fun launchReactionActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, ReactionActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
    }

    fun launchHomeActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
    }

    fun launchTestingActivity() {

    }

    fun launchStatisticsActivity() {

    }
}