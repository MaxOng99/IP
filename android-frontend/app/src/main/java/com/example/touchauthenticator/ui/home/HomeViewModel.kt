package com.example.touchauthenticator.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class HomeViewModel:ViewModel() {

    lateinit var currentUser: FirebaseUser
}