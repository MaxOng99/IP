package com.example.touchauthenticator.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.touchauthenticator.R
import com.example.touchauthenticator.utility.ActivityLauncher
import com.example.touchauthenticator.utility.ServiceLocator
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class AuthActivity:AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private var viewModel: AuthViewModel = AuthViewModel(ServiceLocator.getUserRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        email = findViewById(R.id.username)
        password = findViewById(R.id.password)
    }

    private fun getCredentials(): Pair<String, String> {
        val email = this.email.text.toString() + "@test.com"
        val password = this.password.text.toString()
        return Pair(email, password)
    }

    fun login(view:View) {
        val credentials = getCredentials()
        viewModel.checkIfUserExists(credentials.first, credentials.second)
            .addOnCompleteListener(this) { task ->
                checkResult(task)
            }
    }

    fun signUp(view:View) {
        val credentials = getCredentials()
        viewModel.createNewUser(credentials.first, credentials.second)
            .addOnCompleteListener(this) { task ->
                checkResult(task)
            }
    }

    private fun checkResult(authTask: Task<AuthResult>) {
        if (authTask.isSuccessful) {
            Toast.makeText(baseContext, "Authentication success.",
                Toast.LENGTH_SHORT).show()
            viewModel.storeJwtToken()
            viewModel.storeUserId()
            val currentUser = viewModel.getAuthorizedUser()
            ActivityLauncher.launchHomeActivity(this, currentUser!!)
        } else {
            Toast.makeText(baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT).show()
        }
    }
}