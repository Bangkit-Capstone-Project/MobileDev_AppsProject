package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

	@field:SerializedName("data")
	val data: DataToken,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataToken(

	@field:SerializedName("accessToken")
	val accessToken: String
)
