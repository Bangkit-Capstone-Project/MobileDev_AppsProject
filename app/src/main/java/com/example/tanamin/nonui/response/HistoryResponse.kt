package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: DataHistory,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class PredictionHistorysItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("diseasesName")
	val diseasesName: String,

	@field:SerializedName("diseasesDescription")
	val diseasesDescription: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("accuracy")
	val accuracy: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("plantName")
	val plantName: String
)

data class DataHistory(

	@field:SerializedName("predictionHistorys")
	val predictionHistorys: List<PredictionHistorysItem>
)
