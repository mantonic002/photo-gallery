package com.example.photogallery.api

import com.example.photogallery.model.Photo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

    @GET("/photos")
    suspend fun getPhotos(  @Query("limit") limit: Int,
                            @Query("lastId") lastId:String):
            Response<List<Photo>>
}