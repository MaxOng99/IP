package com.example.touchauthenticator.ui.result

import androidx.lifecycle.ViewModel
import com.example.touchauthenticator.data.model.ResponseWrapper
import com.google.firebase.auth.FirebaseUser

class ResultViewModel:ViewModel() {

    lateinit var currentUser: FirebaseUser
    lateinit var predictionResult: ResponseWrapper
    lateinit var nextActivity: String
    lateinit var authResult: String
}