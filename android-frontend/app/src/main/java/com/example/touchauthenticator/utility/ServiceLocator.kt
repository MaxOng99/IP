package com.example.touchauthenticator.utility

import com.example.touchauthenticator.data.api.AuthApi
import com.example.touchauthenticator.data.repository.UserRepository

object ServiceLocator {
    private fun getAuthApi(): AuthApi = AuthApi()
    fun getUserRepository(): UserRepository = UserRepository(this.getAuthApi())
}