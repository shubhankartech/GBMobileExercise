package com.example.gasbuddysample.api

import com.example.gasbuddysample.model.PicsumImage
import com.example.gasbuddysample.model.PicsumPostResponse
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.util.logging.Logger


interface PicsumService {

    @GET("/list")
    suspend fun fetchPosts(
        @Query("page") page: String = "1",
        @Query("limit") limit: Int? = 20
    ): Response<List<PicsumImage>>

    @GET("/id/{id}/info")
    suspend fun getImageDetail(
        @Path("id") id: Int = 1
    ): Response<PicsumImage>


    companion object {
        fun getService(): PicsumService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://picsum.photos/v2/")

                .addConverterFactory(GsonConverterFactory.create())

                .build()
            return retrofit.create(PicsumService::class.java)
        }
    }


}