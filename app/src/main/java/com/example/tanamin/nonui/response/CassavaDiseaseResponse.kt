package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class CassavaDiseaseResponse(

	@field:SerializedName("data")
	val data: DataCassava,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataCassava(

	@field:SerializedName("result")
	val result: ResultCassava
)

data class ResultCassava(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("diseasesName")
	val diseasesName: String,

	@field:SerializedName("historyId")
	val historyId: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("accuracy")
	val accuracy: Double,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("plantName")
	val plantName: String
)
