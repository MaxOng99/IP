package com.example.touchauthenticator.ui.result

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class ResultViewModel:ViewModel() {

    lateinit var currentUser: FirebaseUser
    lateinit var predictionResult: HashMap<String, String>
    lateinit var nextActivity: String
}