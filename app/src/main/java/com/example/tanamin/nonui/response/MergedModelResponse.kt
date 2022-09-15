package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class MergedModelResponse(

	@field:SerializedName("data")
	val data: Dataclass,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class ResultMerged(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("diseasesName")
	val diseasesName: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("accuracy")
	val accuracy: Double,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("plantName")
	val plantName: String
)

data class Dataclass(

	@field:SerializedName("result")
	val result: ResultMerged
)
