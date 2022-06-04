package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class UserDataResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("status")
	val status: String
)

data class DataUser(

	@field:SerializedName("user")
	val user: User
)


data class User(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("profile_pic_url")
	val profilePicUrl: String,

	@field:SerializedName("username")
	val username: String
)
