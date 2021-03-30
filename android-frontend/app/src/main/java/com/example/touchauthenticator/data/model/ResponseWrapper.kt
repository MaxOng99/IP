package com.example.touchauthenticator.data.model

data class ResponseWrapper(
    val predictions: HashMap<Int, Int>,
    val far: Float,
    val frr: Float,
    val eer: Float
)