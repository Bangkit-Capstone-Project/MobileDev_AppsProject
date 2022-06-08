package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class DeleteRefreshTokenResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)
