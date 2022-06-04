package com.example.tanamin.nonui.api

import com.example.tanamin.nonui.response.AllDeseaseResponse
import com.example.tanamin.nonui.response.LoginResponse
import com.example.tanamin.nonui.response.RegisterResponse
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

    //Desease
    @GET("diseases")
    fun getAllDeseases():Call<AllDeseaseResponse>

    @GET("diseases")
    fun searchDesease(
        @Query("q") query: String
    ):Call<AllDeseaseResponse>
}