package com.example.photogallery.model

data class Photo(
    val ContentType: String,
    val FilePath: String,
    val ID: String,
    val LonLat: LonLat,
    val Metadata: Any,
    val Size: Int,
    val TakenAt: String,
    val ThumbnailPath: String
)