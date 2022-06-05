package com.example.tanamin.nonui.api

import com.example.tanamin.nonui.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //REGISTERING
    @FormUrlEncoded
    @POST("users")
    fun registerUser(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("fullname") fullname: String,
        ): Call<RegisterResponse>

    //LOGIN
    @FormUrlEncoded
    @POST("authentications")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    //DISEASE
    @GET("diseases")
    fun getAllDeseases(
    ):Call<AllDiseasesResponse>

    @GET("diseases")
    fun searchDesease(
        @Query("q") query: String
    ):Call<DataDisease>
}