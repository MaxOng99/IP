package com.example.touchauthenticator.data.model

data class RequestWrapper(
    val experiment_type: String,
    val user_tgd_map: HashMap<Int, ArrayList<TouchGestureData>>
)