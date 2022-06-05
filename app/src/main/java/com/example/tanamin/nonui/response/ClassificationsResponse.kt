package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class ClassificationsResponse(

	@field:SerializedName("data")
	val data: DataResult,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataResult(

	@field:SerializedName("result")
	val result: Result
)

data class Result(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("vegetableName")
	val vegetableName: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("accuracy")
	val accuracy: String,

	@field:SerializedName("description")
	val description: String
)
