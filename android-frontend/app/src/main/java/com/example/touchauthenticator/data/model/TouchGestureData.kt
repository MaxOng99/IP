package com.example.touchauthenticator.data.model

data class TouchGestureData(
    var data: List<HashMap<String, Number>>,
    var date: String,
    var sampleId: Int,
) {

    class RawData(
        var index: Int,
        var pressure: Float,
        var timestamp: Long,
        var x: Float,
        var y: Float
    ) {

        override fun toString(): String {
            return "Index: $index | Timestamp: $timestamp | Pressure: $pressure | X: $x | Y: $y"
        }

    }
}