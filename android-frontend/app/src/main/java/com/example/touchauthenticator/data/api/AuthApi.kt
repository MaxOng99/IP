package com.example.touchauthenticator.data.api

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthApi {

    private  val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email:String, password:String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email:String, password:String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}