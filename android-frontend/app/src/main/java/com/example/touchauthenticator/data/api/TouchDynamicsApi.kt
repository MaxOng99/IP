package com.example.touchauthenticator.data.api

import com.example.touchauthenticator.data.model.RequestWrapper
import com.example.touchauthenticator.data.model.ResponseWrapper
import com.example.touchauthenticator.data.model.TouchGestureData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import retrofit2.Response
import retrofit2.http.*


interface TouchDynamicsApi {

    @POST("predict/reaction")
    suspend fun getReactionPrediction(
        @Header("jwt-token") token:String,
        @Body request: RequestWrapper
    ): Response<ResponseWrapper>

    @POST("predict/keystroke")
    suspend fun getKeystrokePrediction(
        @Header("jwt-token") token: String,
        @Body request: RequestWrapper
    ): Response<ResponseWrapper>

    @GET("/")
    suspend fun getRoot(): Response<HashMap<String, String>>
}