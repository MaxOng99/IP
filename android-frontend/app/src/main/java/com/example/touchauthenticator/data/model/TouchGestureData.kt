package com.example.touchauthenticator.data.model


data class TouchGestureData(

    private var index: Int,
    private var pressure:Float,
    private var timestamp:Long,
    private var x: Float,
    private var y: Float
) {
    override fun toString(): String {
        return "Index: $index | Timestamp: $timestamp | Pressure: $pressure | X: $x | Y: $y"
    }
}