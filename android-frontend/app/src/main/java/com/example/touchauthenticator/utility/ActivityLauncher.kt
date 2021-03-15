package com.example.touchauthenticator.utility

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.touchauthenticator.ui.auth.AuthActivity
import com.example.touchauthenticator.ui.enrolment.KeystrokeEnrolmentActivity
import com.example.touchauthenticator.ui.enrolment.ReactionEnrolmentActivity
import com.example.touchauthenticator.ui.home.HomeActivity
import com.example.touchauthenticator.ui.result.ResultActivity
import com.example.touchauthenticator.ui.test.KeystrokeTestActivity
import com.example.touchauthenticator.ui.test.ReactionTestActivity
import com.google.firebase.auth.FirebaseUser

object ActivityLauncher {

    fun launchKeystrokeActivity(activity: AppCompatActivity, currentUser:FirebaseUser) {
        val intent = Intent(activity, KeystrokeEnrolmentActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchReactionEnrolmentActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, ReactionEnrolmentActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchHomeActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchAuthActivity(activity: AppCompatActivity) {
        val intent = Intent(activity, AuthActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchReactionTestActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, ReactionTestActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchKeystrokeTestActivity(activity: AppCompatActivity, currentUser: FirebaseUser) {
        val intent = Intent(activity, KeystrokeTestActivity::class.java)
        intent.putExtra("user", currentUser)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchResultActivity(activity: AppCompatActivity, currentUser: FirebaseUser, predictionResult: HashMap<String, String>, nextActivity:String) {
        val intent = Intent(activity, ResultActivity::class.java)
        intent.putExtra("user", currentUser)
        intent.putExtra("predictionResults", predictionResult)
        intent.putExtra("nextActivity", nextActivity)
        activity.startActivity(intent)
        activity.finish()
    }

    fun launchStatisticsActivity() {

    }
}