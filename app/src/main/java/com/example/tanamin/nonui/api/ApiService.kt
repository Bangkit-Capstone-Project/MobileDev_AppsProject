package com.example.tanamin.nonui.api

import com.example.tanamin.nonui.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}