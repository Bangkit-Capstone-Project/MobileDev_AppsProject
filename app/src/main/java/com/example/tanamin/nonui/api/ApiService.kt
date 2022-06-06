package com.example.tanamin.nonui.api

import com.example.tanamin.nonui.response.*
import okhttp3.MultipartBody
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

    //UPLOAD PHOTO
    @Multipart
    @POST("upload/pictures")
    fun uploadPhoto(
        @Part file: MultipartBody.Part
    ): Call<UploadFileResponse>

    //FOR THE VEGETABLE FEATURE
    @FormUrlEncoded
    @POST("classifications")
    fun getVegetableClassification(
        @Header("Authorization") token: String,
        @Field("imgUrl") imgUrl: String,
        @Field("endpoint") endpoint: String
    ): Call<ClassificationsResponse>

}