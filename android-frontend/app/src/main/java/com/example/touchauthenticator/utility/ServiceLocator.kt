package com.example.touchauthenticator.utility

import com.example.touchauthenticator.data.api.*
import com.example.touchauthenticator.data.repository.TouchGestureRepository
import com.example.touchauthenticator.data.repository.UserRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private val authApi = AuthApi()
    private val databaseApi = DatabaseApi()

    /*
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(ApiInterceptor())
    }.build()
     */

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GlobalVars.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val touchDynamicsInstance: TouchDynamicsApi by lazy {
        retrofit.create(TouchDynamicsApi::class.java)
    }

    fun getUserRepository(): UserRepository = UserRepository(authApi)
    fun getTouchGestureRepository(): TouchGestureRepository = TouchGestureRepository(databaseApi)
    fun getTouchDynamicsApi(): TouchDynamicsApi {
        return touchDynamicsInstance
    }
}