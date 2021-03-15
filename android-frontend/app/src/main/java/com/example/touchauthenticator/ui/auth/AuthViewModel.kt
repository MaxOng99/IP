package com.example.touchauthenticator.ui.auth

import androidx.lifecycle.ViewModel
import com.example.touchauthenticator.data.repository.UserRepository
import com.example.touchauthenticator.utility.GlobalVars.Companion.JWT_TOKEN
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(
    private val userRepository: UserRepository

):ViewModel() {

    fun checkIfUserExists(email:String, password:String): Task<AuthResult> {
        return userRepository.getUser(email, password)
    }

    fun createNewUser(email:String, password: String): Task<AuthResult> {
        return userRepository.createNewUser(email, password)
    }

    fun getAuthorizedUser(): FirebaseUser? {
        return userRepository.getAuthorizedUser()
    }

    fun storeJwtToken() {
        val authorizedUser = userRepository.getAuthorizedUser()!!
        authorizedUser.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    JWT_TOKEN = task.result!!.token!!
                }
            }
    }
}