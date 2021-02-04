package com.example.touchauthenticator.data.repository

import com.example.touchauthenticator.data.api.AuthApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class UserRepository (
    private val authApi: AuthApi
) {

    fun getUser(email:String, password:String): Task<AuthResult> {
        return authApi.login(email, password)
    }

    fun createNewUser(email:String, password: String): Task<AuthResult> {
        return authApi.signUp(email, password)
    }

    fun getAuthorizedUser(): FirebaseUser? {
        return authApi.getCurrentUser()
    }

}