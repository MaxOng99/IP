package com.example.touchauthenticator.utility

import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.api.AuthApi
import com.example.touchauthenticator.data.api.DatabaseApi
import com.example.touchauthenticator.data.repository.UserRepository
import com.example.touchauthenticator.ui.auth.TouchDynamicsApi

object ServiceLocator {
    private val authApi = AuthApi()
    private val databaseApi = DatabaseApi()

    fun getUserRepository(): UserRepository = UserRepository(authApi)
    fun getTouchGestureRepository(): TouchGestureRepository = TouchGestureRepository(databaseApi)
    fun getTouchDynamicsApi(): TouchDynamicsApi = TouchDynamicsApi()

}