package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class TomatoDiseaseResponse(

	@field:SerializedName("data")
	val data: DataTomato,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataTomato(

	@field:SerializedName("result")
	val result: ResultTomato
)

data class ResultTomato	(

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
