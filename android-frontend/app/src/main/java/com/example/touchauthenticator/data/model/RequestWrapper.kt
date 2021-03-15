package com.example.touchauthenticator.data.model

data class RequestWrapper(
    val request: HashMap<Int, ArrayList<TouchGestureData>>
)